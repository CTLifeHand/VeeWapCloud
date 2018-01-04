package com.veewap.app.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.veewap.dao.*;
import com.veewap.dao.impl.*;
import com.veewap.domain.*;
import com.veewap.util.CommonsWeatherUtils;
import com.veewap.util.TCUtil;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

// ws://192.168.3.3:8080/websocket

//该注解用来指定一个URI，客户端可以通过这个URI来连接到WebSocket。类似Servlet的注解mapping。无需在web.xml中配置。
@ServerEndpoint("/websocket/veewap")
public class VWWebSocket {
	// 不是单例来的 每个连接都是一个新的实例
	public VWWebSocket() {
//		System.out.println("新实例");
	}

	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	// TODO : 其他
	private static JSONObject cityWeathersJson = new JSONObject();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	// TODO : 定时器
	private static Timer ConnectTimeoutTimer = null;
	private static Timer refreshWeatherTimer = null;

	// TODO : DAO
	private IVMHomeDAO homeDAO = new VMHomeDAOImpl();
	private IVWVersionDAO versionDAO = new VWVersionDAOImpl();
	private IVMHomeUserDAO homeUserDAO = new VMHomeUserDAOImpl();
	private IVMUserDAO userDAO = new VMUserDAOImpl();
	private IVWHomeNoticeDAO noticeDAO = new VWHomeNoticeDAOImpl();
	private IVWFeedbackDAO feedbackDAO = new VWFeedbackDAOImpl();



	// TODO : 管理
	// OnOpen OnClose会自动维护这个
	private static volatile HashMap<String, VWWebSocketClient> clients = new HashMap<>();
	// 使用homeID作为key 这个需要自己手动维护
	private static volatile HashMap<String, VWWebSocketClient> homeServers = new HashMap<>();
	// 使用 username 作为key
	private static volatile HashMap<String, VWWebSocketClient> userClients = new HashMap<>();




	// 超时定时器的代码
	private void connectTime() {
		if (ConnectTimeoutTimer == null) {
			ConnectTimeoutTimer = new Timer();
			ConnectTimeoutTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					synchronized (clients) {
						try {

							long currTime = new Date().getTime();
							Iterator<?> iter = clients.entrySet().iterator();
							while (iter.hasNext()) {
								Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
								VWWebSocketClient server = (VWWebSocketClient) entry.getValue();
								if (server.isClientServer && currTime - server.lastMessageTime > 61000) {
									System.out.println(
											"ConnectTimeoutTimer remove connection, homeid is" + server.VMHomeId);
									iter.remove();
									// if(val.isClientServer &&
									// homeServers.containsKey(val.VMHomeId +
									// "")){
									// homeServers.remove(val.VMHomeId + "");
									// }
									server.close();
									if (server.isClientServer && homeServers.containsKey(server.VMHomeId + "")) {
										homeServers.remove(server.VMHomeId);
										homeDAO.offlineHome(server.VMHomeId); // 将家庭置为离线状态
										System.out.println("Server disconnect:" + server.VMHomeId + " at "
												+ dateFormat.format(new Date()));

										// 服务器端退出登录时，通知已连接本服务器的客户端连接断开
										String message = "{\"type\":\"ServerDisconnected\"}";
										Iterator<?> iter2 = clients.entrySet().iterator();
										while (iter2.hasNext()) {
											Map.Entry<?, ?> entry2 = (Map.Entry<?, ?>) iter2.next();
											VWWebSocketClient client = (VWWebSocketClient) entry2.getValue();
											if (!client.isClientServer && client.VMHomeId > -1
													&& client.VMHomeId == server.VMHomeId) {
												client.sendMessage(message);
											}
										}

									}

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, 5000, 5000);
		}
	}

	// 刷新天气和删除通知定时器的代码
	private void refreshTime() {
		if (refreshWeatherTimer == null) {
			refreshWeatherTimer = new Timer();
			refreshWeatherTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					refreshWeathers();
					noticeDAO.deleteOverdueNotice(3); // 删除过期通知
				}
			}, 0, (90000000 - System.currentTimeMillis() % 86400000)); // 每天凌晨3点更新一次天气情况
		}
	}

	public static void refreshWeathers() {
		synchronized (cityWeathersJson) {
			Set<String> set = cityWeathersJson.keySet();
			for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
				String cityName = (String) iterator.next();
				refreshWeather(cityName);
			}
		}

		synchronized (homeServers) {
			Iterator<VWWebSocketClient> iterator = homeServers.values().iterator();
			while (iterator.hasNext()) {
				VWWebSocketClient endpoint = iterator.next();
				if (cityWeathersJson.containsKey(endpoint.VMCity)) {
					String weather = cityWeathersJson.getJSONObject(endpoint.VMCity).getString("weather");
					if (!weather.equals(endpoint.VMWeather)) {

						JSONObject weatherMsg = new JSONObject();
						weatherMsg.put("type", "RefreshWeather");
						weatherMsg.put("VMWeather", weather);
						endpoint.sendMessage(weatherMsg.toJSONString());
					}
				}
			}
		}
	}

	public static boolean changeHomeManager(long homeId, int userId) {
		String homeIdStr = String.valueOf(homeId);
		if (homeServers.containsKey(homeIdStr)) {
			VWWebSocketClient endpoint = homeServers.get(homeIdStr);
			String msg = "{\"type\":\"ChangeHomeManager\",\"userId\":" + userId + "}";
			System.out.println("send message to server: " + msg);
			endpoint.sendMessage(msg);
			return true;
		} else {
			System.out.println("Change home manager failed, no homeId:" + homeIdStr + ", home serves count is:"
					+ homeServers.size());
			return false;
		}

	}

	public static boolean changeServerPanel(long homeId, int serverPanelId) {
		String homeIdStr = String.valueOf(homeId);
		if (homeServers.containsKey(homeIdStr)) {
			VWWebSocketClient endpoint = homeServers.get(homeIdStr);
			String msg = "{\"type\":\"ChangeServerPanel\",\"panelId\":" + serverPanelId + "}";
			System.out.println("send message to server: " + msg);
			endpoint.sendMessage(msg);
			return true;
		} else {
			System.out.println("Change server panel failed, no homeId:" + homeIdStr + ", home serves count is:"
					+ homeServers.size());
			return false;
		}
	}

	// 发送用户给panel
	public static boolean cloudSyncSendUser(VMHomeUser user) {
		Long homeId = user.getHomeId();
		if (!homeServers.containsKey(homeId + "")) { // 家庭不在线
			return false;
		} else {
			VWWebSocketClient client = homeServers.get(homeId + "");
			JSONArray userArray = client.userArray;
			for (int i = 0; i < userArray.size(); i++) {
				// 发送同步用户消息，注意state为0表示用户可用（0：正常使用 1：已添加待确认 2：被删除）
				JSONObject userJSON = userArray.getJSONObject(i);
				if (userJSON.containsKey("name") && (userJSON.getString("name").equals(user.getUserName()))) {
					user.setId(userJSON.getString("id"));
					user.setType("401");
					JSONObject message = new JSONObject();
					message.put("type", "SendMessage");
					message.put("data", user);
					System.out.println("同步用户" + user.toString());
					client.sendMessage(message.toJSONString());
					return true;
				}
			}
			return true;
		}
	}
	
	public static void sendAppClientUserMessage(String senduser, String message){
		if(userClients.containsKey(senduser)){
			userClients.get(senduser).sendMessage(message);
		}
	}

	/**
	 * 连接建立成功调用的方法 注意 ID 是生命周期内一直增加的
	 * 
	 * @param session
	 *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session) {
		// 拦截所有session
		session.setMaxTextMessageBufferSize(64 * 1024);// 64*1024 = 64k
		clients.put(session.getId(), new VWWebSocketClient(session));
		// webSocketSet.add(this); //加入set中
		addOnlineCount(); // 在线数加1
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());

		// 暂时不知道初始化的方法 在这里先搞
		connectTime();
		refreshTime();
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 * @param session
	 *            可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println("websocket/veewap 来自客户端的消息:" + message);
		// System.out.println(lastMessageTime);
		JSONObject json;
		try {
			json = JSON.parseObject(message);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println(message);
			return;
		}

		VWWebSocketClient client = getClint(session);

		invokeAndWait(new Runnable() {
			@Override
			public void run() {
				String messageType = json.getString("type");
				switch (messageType) {
				case "ServerLogin":
					onServerLogin(message, session, json);
					break;
				case "HomeRoomClear": // 家庭房间被清空，将家庭置为虚拟家庭
					homeDAO.setVisual(client.VMHomeId);
					break;
				case "HomeManageMessage":
				case "HomeMessage":
					// 家庭管理信息需要存进数据库，否则内存占用过大
					// homeManageMessage = json;
					/**
					 * @author CYT json是在线家庭每5min更新一次
					 */
					homeDAO.saveHomeMessage(client.VMHomeId, json.toString());
					break;
				case "ServerChangeCity":
					String VMCity = TCUtil.getVMCity(json.containsKey("VMCity") ? json.getString("VMCity") : "");
					client.VMCity = VMCity;
					// 如果有城市信息，即时尝试获取天气
					onServerChangeCity(VMCity, session);
					break;
				case "ServerAddUser":
					onServerAddUser(message, session, json);
					break;
				case "ServerResponse": // 服务器对客户端的申请做响应
					String sessionId = json.getString("clientCode");
					if (clients.containsKey(sessionId))
						clients.get(sessionId).sendMessage(message);
					break;
				// 服务器因为各种客户端的操作数据更新 (相当于回来的信息 客户端只有在远程才处理 应用场景只有 2个客户端都在远程的情况
				// panel端应该每次都发ServerRefresh)
				// ServerRefresh 是panel端接受普通协议就往云端发
				case "ServerRefresh":
					onServerRefresh(message, session, json);
					break;
				case "ClientLogin":// 客户端登录 (握手成功后)
					onClientLogin(message, session, json);
					break;
				case "ChangeServer":// 客户端更换连接服务器(登录成功后)
					onChangeServer(message, session, json);
					break;
				case "ClientRequest":// 客户端向服务器发送信息(进入主界面后)
					onClientRequest(message, session, json);
					break;
				case "Freeback": // 兼容以前写错的英文
				case "Feedback":
					JSONArray feedbackArray = json.getJSONArray("data");
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String nowTime = dateFormat.format(new Date());
					// 存入数据库
					for (int i = 0; i < feedbackArray.size(); i++) {
						JSONObject feedbackJson = feedbackArray.getJSONObject(i);
						String mobile = feedbackJson.getString("mobile");
						String feedback = feedbackJson.getString("sugesstion");
						VWFeedback feedbackObject = new VWFeedback();
						feedbackObject.setHomeId(client.VMHomeId + "");
						feedbackObject.setMobile(mobile);
						feedbackObject.setFeedback(feedback);
						feedbackObject.setCreateTime(nowTime);
						feedbackDAO.save(feedbackObject);
					}
					break;
				default:
					break;
				}
				client.lastMessageTime = new Date().getTime();
			}

		});
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(Session session) {
		clients.remove(session.getId());
		; // 从set中删除
		subOnlineCount(); // 在线数减1
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

	// 远程手机连接的时候同步用(panel发给云)
	private void onServerRefresh(String message, Session session, JSONObject json) {
		VWWebSocketClient client = getClint(session);
		Iterator<Entry<String, VWWebSocketClient>> iter = clients.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
			VWWebSocketClient val = (VWWebSocketClient) entry.getValue();
			if (!val.isClientServer && val.VMHomeId > -1 && val.VMHomeId == client.VMHomeId) {
				val.sendMessage(message);
			}
		}

		/**
		 * 特殊情况，特殊处理: 401消息添加用户时，需更新数据库信息;402删除用户时，数据库也得删除用户与家庭的关系
		 * 修改家庭名称消息，需更新数据库及当前内存信息
		 */
		JSONObject messageJson = json.getJSONObject("data");
		if (messageJson.containsKey("type") && messageJson.getIntValue("type") == 401) {
			addUserWithJSONObject(session, messageJson);
		} else if (messageJson.containsKey("type") && messageJson.getIntValue("type") == 402) {
			if (messageJson.containsKey("id")) {
				deleteUserWithID(session, messageJson.getIntValue("id"));
			}
		} else if (messageJson.containsKey("type") && messageJson.getIntValue("type") == 1) {
			refreshHome(session, messageJson);
		}

	}

	private void addUserWithJSONObject(Session session, JSONObject messageJson) {
		VWWebSocketClient client = getClint(session);
		client.userArray.add(messageJson); // 将用户加入用户列表
		addUserToDataBaseWithUserObject(messageJson, client.VMHomeId);
		// 查出密码
		VMHomeUser user = homeUserDAO.getSyncUserMessage(client.VMHomeId, messageJson.getString("name"));
		// 同步会panel
		cloudSyncSendUser(user);
	}

	private void deleteUserWithID(Session session, int userID) {
		VWWebSocketClient client = getClint(session);
		String username = "";
		for (int i = 0; i < client.userArray.size(); i++) {
			JSONObject user = client.userArray.getJSONObject(i);
			if (user.getIntValue("id") == userID) {
				username = user.getString("name");
				homeUserDAO.delete(client.VMHomeId, username);
				client.userArray.remove(i);
				break;
			}
		}

		// 删除用户成功，如果用户在线，并且是当前家庭用户，发送被踢出家庭(iOS好似没做)
		if (!username.isEmpty() && userClients.containsKey(username)) {
			VWWebSocketClient endpoint = userClients.get(username);
			if (endpoint.VMHomeId == client.VMHomeId) { // 当前家庭
				endpoint.sendMessage("{\"type\":\"KickOutHome\"}");
			}
		}
		// TODO 生成通知，已被移除家庭？
	}

	private void refreshHome(Session session, JSONObject messageJson) {
		VWWebSocketClient client = getClint(session);
		if (messageJson.containsKey("VMHomeName")) {
			client.VMHomeName = messageJson.getString("VMHomeName");
		}
		if (messageJson.containsKey("VMHomeAddress")) {
			client.VMHomeAddress = messageJson.getString("VMHomeAddress");
		}
		if (messageJson.containsKey("VMCity")) {
			client.VMCity = messageJson.getString("VMCity");
		}
		VMHome home = homeDAO.get(client.VMHomeId);
		if (home == null) {
			home = new VMHome();
			home.setId(client.VMHomeId);
		}
		home.setHomeName(client.VMHomeName);
		home.setCity(client.VMCity);
		home.setHomeAddress(client.VMHomeAddress);
		home.setServerPanelId(client.ServerPanelId);
		home.setIsOnline("1");
		home.setIsVisual(0);
		int line = homeDAO.update(home);
		if (line != 0) {
			homeDAO.save(home);
		}
	}

	// 同步天气
	private void onServerChangeCity(String VMCity, Session session) {
		VWWebSocketClient client = getClint(session);
		JSONObject cityJson = getWeather(VMCity);
		if (cityJson != null && !cityJson.isEmpty() && cityJson.containsKey("weather")
				&& !client.VMWeather.equals(cityJson.getString("weather"))) {
			client.VMWeather = cityJson.getString("weather");
			JSONObject weatherMsg = new JSONObject();
			weatherMsg.put("type", "RefreshWeather");
			weatherMsg.put("VMWeather", cityJson.getString("weather"));
			sendMessage(weatherMsg.toJSONString(), session);
		}
	}

	private void addUserToDataBaseWithUserObject(JSONObject userJson, long VMHomeId) {
		String username = userJson.containsKey("name") ? userJson.getString("name") : "";
		String password = ""; // 如果没有填写密码，设置空字符串
		if (userJson.containsKey("password") && userJson.get("password") != null)
			password = userJson.getString("password");
		if (TCUtil.isMobileNO(username)) {// 非手机用户，不录入系统
			// 查看有无该用户
			VMUser user = userDAO.get(username);
			if (user == null) {
				user = new VMUser();
				user.setUserName(username);
				user.setPassword(password);
				userDAO.save(user);
			} else {// 已经有该用户了

			}
		}

		if (!userJson.containsKey("state") || userJson.getIntValue("state") == 0) { // 只有状态为0的可用用户状态，才写入云端数据库
			VMHomeUser homeUser = new VMHomeUser();
			homeUser.setHomeId(VMHomeId);
			homeUser.setUserName(username);
			int line = homeUserDAO.update(homeUser);
			if (line == 0) {
				homeUser.setPermission("10");
				homeUserDAO.save(homeUser);
			}
		}
	}

	// 同步用户 (先从客户端同步到VMUser)
	private void onServerAddUser(String message, Session session, JSONObject json) {
		VWWebSocketClient client = getClint(session);
		JSONArray userArray = json.getJSONArray("VMUserArray");
		client.userArray = userArray;

		Long VMHomeId = client.VMHomeId;
		// 先把用户从panel同步过来
		for (int i = 0; i < userArray.size(); i++) {
			JSONObject userJson = userArray.getJSONObject(i);
			addUserToDataBaseWithUserObject(userJson, VMHomeId);
		}
		// 数据库用户密码同步到面板端、面板端可能有新增用户同步到数据库
		// 先查出所有用户
		List<VMHomeUser> dataBaseUser = homeUserDAO.getListUserMessage(VMHomeId);
		for (VMHomeUser vmHomeUser : dataBaseUser) {
			int state = Integer.parseInt(vmHomeUser.getState());
			if (state == 0) {
				// 查看是否需要删除 (所以是以本地的数据为主)
				// TODO:所以是以本地的数据为主
				boolean isDelete = true;
				for (int i = 0; i < userArray.size(); i++) {
					if (userArray.getJSONObject(i).containsKey("name")
							&& userArray.getJSONObject(i).getString("name").equals(vmHomeUser.getUserName())) {
						isDelete = false;
						break;
					}
					if (isDelete) { // 删除用户关系
						homeUserDAO.delete(VMHomeId, vmHomeUser.getUserName());
					}

				}
				// TODO :
				// 不管有无改变密码都发过去
				cloudSyncSendUser(vmHomeUser);
			} else if (state == 1) { // state为1的时候不处理，等待通知的结果
				// 不会同步给Panel
			} else if (state == 2) {// 2:需要添加用户 3：需要删除用户
				// 变成0
				vmHomeUser.setState("0");
				// Panel更新用户
				homeUserDAO.save(vmHomeUser);
				cloudSyncSendUser(vmHomeUser);
			} else if (state == 3) {
				// 直接删除用户
				homeUserDAO.delete(vmHomeUser.getHomeId(), vmHomeUser.getUserName());
				// TODO: 状态3应该是直接删除的 不用发

			}
		}
	}

	// 客户端更换连接服务器
	private void onChangeServer(String message, Session session, JSONObject json) {
		VWWebSocketClient client = getClint(session);
		long VMHomeId = json.getLong("VMHomeId");
		client.VMHomeId = VMHomeId;
		client.VMHomeName = json.getString("VMHomeName");

		if (homeServers.containsKey(VMHomeId + "")) {
			json.put("success", true);
			client.sendMessage(json.toString());
			System.out.println("Client connect:" + VMHomeId + " at " + dateFormat.format(new Date()));
		} else {
			json.put("success", false);
			client.sendMessage(json.toString());
			System.out.println("NO Server:" + VMHomeId + "");
		}
	}

	// 客户端向服务器发送信息(这个方法应该在"ChangeServer"之后)
	// 只是简单地转发一下
	private void onClientRequest(String message, Session session, JSONObject json) {
		VWWebSocketClient client = getClint(session);
		json.put("clientCode", session.getId());
		json.put("username", client.username);

		String VMHomeServerKey = client.VMHomeId + "";
		if (homeServers.containsKey(VMHomeServerKey)) {
			homeServers.get(VMHomeServerKey).sendMessage(json.toString());
		}
	}

	// 客户端登录
	private void onClientLogin(String message, Session session, JSONObject json) {
		VWWebSocketClient client = getClint(session);
		String username = json.getString("UserName");
		client.username = username;
		String password = json.getString("Password");
		VMUser user = userDAO.get(username);
		if (user == null) { // 没有该用户
			json.put("success", false);
			client.sendMessage(json.toString());
		} else if (user.getPassword() == null || user.getPassword().isEmpty()) {// 需要重置密码or第一次登录
			json.put("type", "ResetPassword");
			client.sendMessage(json.toString());
			userClients.put(username, client);
		} else if (user.getPassword().equals(password)) { // 密码正确
			List<VMHome> myHomeServers = homeDAO.getHomeServers(username);
			json.put("success", true);
			json.put("MyHomeServers", myHomeServers);
			client.sendMessage(json.toString());
			System.out.println("websocket/veewap 向客户端发送通知" + json.toJSONString());
			userClients.put(username, client);
			System.out.println("Client login:" + username + " at " + dateFormat.format(new Date()));
		} else {
			json.put("success", false);
			client.sendMessage(json.toString());
		}

		// 手机端登录时，查找数据库看是否有未处理通知消息，发送到手机端
		List<VWHomeNotice> notices = noticeDAO.getNoticesWithUsername(username);
		if (notices.size() > 0) {
			json = new JSONObject();
			json.put("type", "HomeNotices");
			json.put("notices", notices);
			client.sendMessage(json.toString());
		}
		System.out.println("websocket/veewap 向客户端发送通知" + json.toJSONString());
	}

	private void onServerLogin(String message, Session session, JSONObject json) {
		VWWebSocketClient client = getClint(session);
		client.isClientServer = true;
		Long VMHomeId = json.getLong("VMHomeId");
		client.VMHomeId = VMHomeId;
		client.VMHomeName = json.getString("VMHomeName");
		String VMCity = TCUtil.getVMCity(json.containsKey("VMCity") ? json.getString("VMCity") : "");
		client.VMCity = VMCity;
		client.VMHomeAddress = json.containsKey("VMHomeAddress") ? json.getString("VMHomeAddress") : "";
		client.ServerPanelId = json.containsKey("ServerPanelId") ? json.getIntValue("ServerPanelId") : -1;
		client.VMWeather = json.containsKey("VMWeather") ? json.getString("VMWeather") : "";


		// 与数据库同步家庭数据，此时将家庭置为在线
		VMHome home = homeDAO.get(client.VMHomeId);
		System.out.println(home);
		if (home == null) {
			// 没查到
			home = new VMHome();
			home.setId(VMHomeId);
			home.setHomeName(client.VMHomeName);
			home.setCity(client.VMCity);
			home.setHomeAddress(client.VMHomeAddress);
			home.setServerPanelId(client.ServerPanelId);
			home.setIsOnline("1");
			home.setIsVisual(0);
			homeDAO.save(home);
		} else {
			// 更新
			home.setId(VMHomeId);
			home.setHomeName(client.VMHomeName);
			home.setCity(client.VMCity);
			home.setHomeAddress(client.VMHomeAddress);
			home.setServerPanelId(client.ServerPanelId);
			home.setIsOnline("1");
			home.setIsVisual(0);
			homeDAO.update(home);
		}
		if (home.getIsDelete() == 1) {
			// 直接将json返回给panel
			json.put("type", "HomeDeleted");
			sendMessage(json.toJSONString(), session);
			return;
		}

		if (homeServers.containsKey(VMHomeId + "")) { // 已有服务器端占用此账户
			VWWebSocketClient oldClient = homeServers.get(VMHomeId + "");
			int oldServerID = oldClient.ServerPanelId;
			if (client.ServerPanelId != oldServerID) { // 不是同一台面板连接，则发RepeatLogin消息
				System.out.println("Server repeat login:" + VMHomeId + " at " + dateFormat.format(new Date()));
				// 发送重复登录消息，表示已有设备当做服务器启动，并将当前服务器panelId发回当前连接的设置
				sendMessage("{\"type\":\"RepeatLogin\",\"ServerPanelId\":" + oldServerID + "}", session);
				VMHomeId = -1L; // 不承认此次登录
				return;
			} else { // 是同一台面板连接，证明之前的连接因某些异常关闭了，此时关闭云端的该条连接
				oldClient.isClientServer = false;// 不再在onClose内移除homeServers的该项
				oldClient.close();
				if (homeServers.containsKey(VMHomeId + ""))
					homeServers.remove(VMHomeId + "");
			}
		}

		// 如果有城市信息，即时尝试获取天气
		// TODO: 代码的抽取 看看是否有问题
		onServerChangeCity(VMCity, session);

		// TODO: 服务器端连接时，需同步一些数据，如增加的用户信息等，待完成
		System.out.println("Server connect:" + VMHomeId + " at " + dateFormat.format(new Date()));
		homeServers.put(VMHomeId + "", client);

		// 服务器端登录时，通知已连接的，想要连接本服务器的客户端连接成功
		json.put("type", "ChangeServer");
		json.put("success", true);
		String ChangeServerMessage = json.toString();
		Iterator<?> iter = clients.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
			VWWebSocketClient aClient = (VWWebSocketClient) entry.getValue();
			if (!aClient.isClientServer && aClient.VMHomeId > -1 && aClient.VMHomeId == VMHomeId) {
				aClient.sendMessage(ChangeServerMessage);
			}
		}

		// 同步版本更新数据(这个更新到Panel有什么作用? ..)
		JSONObject homeVersion = json.containsKey("VWVersion") ? json.getJSONObject("VWVersion") : null;
		refreshHomeVersion(homeVersion, session);

		// 邀请模块(直接和Server)
		// TODO: 代码的抽取 看看是否有问题
		onServerAddUser(message, session, json);
	}

	// TODO : Private
	private List<Runnable> cachedRunnables = null;
	private final ReentrantLock taskLock = new ReentrantLock();

	private void invokeAndWait(Runnable task) {
		if (taskLock.isHeldByCurrentThread()) {
			if (cachedRunnables == null) {
				cachedRunnables = new ArrayList<Runnable>();
			}
			cachedRunnables.add(task);
		} else {
			taskLock.lock();
			try {
				cachedRunnables = null;

				// if (!closed) {
				task.run();
				// }
				if (cachedRunnables != null) {
					for (int i = 0; i < cachedRunnables.size(); i++) {
						// if (!closed) {
						cachedRunnables.get(i).run();
						// }
					}
					cachedRunnables = null;
				}

			} finally {
				taskLock.unlock();
			}
		}
	}

	private void sendMessage(String message, Session session) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private VWWebSocketClient getClint(Session session) {
		return clients.get(session.getId());
	}

	private void refreshHomeVersion(JSONObject homeVersion, Session session) {
		VWVersion andriod = versionDAO.getLastVersionWith("0");
		JSONObject refresh = new JSONObject();
		refresh.put("version", andriod.getVersion());
		refresh.put("url", andriod.getPath());

		VWVersion iOS = versionDAO.getLastVersionWith("1");
		refresh.put("iOS", iOS.getVersion());

		boolean isNeedRefreshVersion = false;
		if (homeVersion != null) {
			if (!homeVersion.containsKey("Android")
					|| !homeVersion.getString("Android").equals(refresh.getString("Android"))) {
				isNeedRefreshVersion = true;
			} else if (!homeVersion.containsKey("url")
					|| !homeVersion.getString("url").equals(refresh.getString("url"))) {
				isNeedRefreshVersion = true;
			} else if (!homeVersion.containsKey("iOS")
					|| !homeVersion.getString("iOS").equals(refresh.getString("iOS"))) {
				isNeedRefreshVersion = true;
			}
		} else {
			isNeedRefreshVersion = true;
		}
		if (isNeedRefreshVersion) {
			JSONObject weatherMsg = new JSONObject();
			weatherMsg.put("type", "RefreshVersion");
			weatherMsg.put("VWVersion", refresh.toString());
			sendMessage(weatherMsg.toJSONString(), session);
		}
	}

	private static synchronized int getOnlineCount() {
		return onlineCount;
	}

	private static synchronized void addOnlineCount() {
		VWWebSocket.onlineCount++;
	}

	private static synchronized void subOnlineCount() {
		VWWebSocket.onlineCount--;
	}

	private static JSONObject getWeather(String cityName) {
		if (cityName.isEmpty())
			return null;
		if (!cityWeathersJson.containsKey(cityName)) {
			refreshWeather(cityName);
		}
		return cityWeathersJson.getJSONObject(cityName);
	}

	private static void refreshWeather(String cityName) {
		try {
			String link = "http://php.weather.sina.com.cn/xml.php?city=" + URLEncoder.encode(cityName, "GBK")
					+ "&password=DJOYnieT8234jlsK&day=0";
			URL url = new URL(link);
			CommonsWeatherUtils parser = new CommonsWeatherUtils(url);
			String[] nodes = { "city", "status1", "temperature1", "status2", "temperature2" };
			Map<String, String> map = parser.getValue(nodes);

			JSONObject cityJson = new JSONObject();
			cityJson.put("cityName", cityName);
			cityJson.put("weather", map.get(nodes[1]));

			cityWeathersJson.put(cityName, cityJson);

			System.out.println(map.get(nodes[0]) + " 今天白天：" + map.get(nodes[1]) + " 最高温度：" + map.get(nodes[2])
					+ "℃ 今天夜间：" + map.get(nodes[3]) + " 最低温度：" + map.get(nodes[4]) + "℃ ");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
	}

	public static boolean deleteHome(String homeId) {
		if (!homeServers.containsKey(homeId + "")) { // 家庭不在线
			return false;
		} else {
			VWWebSocketClient val = homeServers.get(homeId + "");
			val.sendMessage("{\"type\":\"HomeDeleted\"}");
			return true;
		}
	}

}