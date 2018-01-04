package com.veewap.app.servlet;

import com.alibaba.fastjson.JSONObject;
import com.veewap.app.websocket.VWWebSocket;
import com.veewap.dao.IVMHomeDAO;
import com.veewap.dao.IVMHomeUserDAO;
import com.veewap.dao.IVMUserDAO;
import com.veewap.dao.IVWHomeNoticeDAO;
import com.veewap.dao.impl.VMHomeDAOImpl;
import com.veewap.dao.impl.VMHomeUserDAOImpl;
import com.veewap.dao.impl.VMUserDAOImpl;
import com.veewap.dao.impl.VWHomeNoticeDAOImpl;
import com.veewap.domain.VMHome;
import com.veewap.domain.VMHomeUser;
import com.veewap.domain.VMUser;
import com.veewap.domain.VWHomeNotice;
import com.veewap.util.JPushUtil;
import com.veewap.util.TCUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


//邀请家人模块(HTTP)
@WebServlet("/VWHomeServlet")
public class VWHomeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private IVMHomeDAO homeDAO = new VMHomeDAOImpl();
	private IVMHomeUserDAO homeUserDAO = new VMHomeUserDAOImpl();
	private IVMUserDAO userDAO = new VMUserDAOImpl();
	private IVWHomeNoticeDAO noticeDAO = new VWHomeNoticeDAOImpl();


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		String type = request.getParameter("type");
		Map<String, Object> objectMap = TCUtil.mapToHashMap(request.getParameterMap());
		JSONObject paras = new JSONObject(objectMap);

		System.out.println("VWHomeServlet====" + paras.toJSONString());

		switch (type) {
		case "backupserver":
			doBackupserver(request, response);
			break;
		case "changeserver":
			doChangeserver(request, response);
			break;
		case "log":
			doLog(request, response);
			break;
		// 这里纯数据库就OK了
		case "CreateHome":
		case "SetHome":
			doSetHome(request, response);
			break;
		case "DeleteHome":
			// 事务问题 还有 业务问题
			doDeleteHome(request, response);
			break;
		case "ExitHome":
			doExitHome(request, response);
			break;
		case "InviteHomeUser":
			doInviteHomeUser(request, response);
			break;
		case "DealInvite":
			doDealInvite(request, response);
			break;
		case "ApplyHomeUser":
			doApplyHomeUser(request, response);
			break;
		case "DealApply":
			doDealApply(request, response);
			break;
		default:
			break;
		}
	}

	private void doBackupserver(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doBackupserver");
	}

	private void doChangeserver(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doChangeserver");
	}

	private void doLog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("doLog");
		String homeId = request.getParameter("homeId");
		String homeName = request.getParameter("homeName");
		String logText = request.getParameter("logText");
		logText = logText.replaceAll("\\|s", " ");
		logText = logText.replaceAll("\\|n", "\n");
		logText = logText.replaceAll("\\|t", "\t");
		System.out.println("Log come from home, homeId:" + homeId + ", homeName:" + homeName);
		System.out.println(logText);
	}

	private void doSetHome(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject json = new JSONObject();
		json.put("result", 0);

		String homeId = request.getParameter("VMHomeId");
		if (homeId == null) {
			json.put("message", "家庭的VMHomeId不能为空！");
			String jsonString = json.toString();
			// System.out.println(jsonString);
			sendMessage(jsonString, response);
			return;
		}

		String username = request.getParameter("username");
		String homeName = request.getParameter("VMHomeName");
		String homeAddress = request.getParameter("VMHomeAddress");
		String homeCity = request.getParameter("VMCity");

		if (username == null || !TCUtil.isMobileNO(username)) {
			json.put("message", "虽然username从App请求不能为null 或者不是手机号");
			String jsonString = json.toString();
			// System.out.println(jsonString);
			sendMessage(jsonString, response);
			return;
		}

		VMHome home = new VMHome();
		home.setId(Long.parseLong(homeId));
		home.setHomeName(homeName);
		home.setHomeAddress(homeAddress);
		home.setCity(homeCity);

		// 无论更新还是新增 都要有这些字段
		home.setServerPanelId(-1);
		home.setIsOnline("0");
		home.setIsVisual(1);
		// if(homeName == null) homeName = "";
		// if(homeAddress == null) homeAddress = "";
		// if(homeCity == null) homeCity = "";
		int line = homeDAO.update(home);
		if (line < 1) {
			// 重复请求 or 真的没有
			line = homeDAO.save(home);
			if (line < 1) {
				System.out.println("重复请求" + home);
			} else {
				System.out.println("插入了" + home);
			}

		} else {
			System.out.println("更新了" + home);
		}

		// 还需要更新homeUser
		VMHomeUser homeUser = new VMHomeUser();
		homeUser.setHomeId(Long.parseLong(homeId));
		homeUser.setUserName(username);
		homeUser.setState("0");
		line = homeUserDAO.update(homeUser);
		if (line < 1) {
			// 重复请求 or 真的没有
			homeUser.setPermission("10");
			line = homeUserDAO.save(homeUser);
			if (line < 1) {
				System.out.println("重复请求" + homeUser);
			} else {
				System.out.println("插入了" + homeUser);
			}
		} else {
			System.out.println("更新了" + homeUser);
		}

		json.put("result", 1);
		json.put("message", "更新成功");
		String jsonString = json.toString();
		sendMessage(jsonString, response);
	}

	private void doDeleteHome(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String homeId = request.getParameter("VMHomeId");
		JSONObject json = new JSONObject();
		if (homeId == null) {
			json.put("result", 0);
			json.put("message", "家庭的VMHomeId不能为空！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}

		// 注意 : 和2张表有关 而且 不能立刻删除VMHome
		int line = homeDAO.deleteVMHome(Long.parseLong(homeId));

		// 向在线家庭发家庭已被删除消息，如果不在线，会在连接时处理删除消息
		VWWebSocket.deleteHome(homeId);

		if (line == 1) {
			json.put("result", 1);
			json.put("message", "成功删除家庭");
		} else {
			json.put("result", 0);
			json.put("message", "没有找到家庭");
		}
		String jsonString = json.toString();
		sendMessage(jsonString, response);
	}

	private void doExitHome(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject json = new JSONObject();
		String homeId = request.getParameter("VMHomeId");
		String username = request.getParameter("username");
		if (homeId == null) {
			json.put("result", 0);
			json.put("message", "家庭的VMHomeId不能为空！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}
		// 这里不是太明白 为啥不能直接删除
		VMHomeUser homeUser = new VMHomeUser();
		homeUser.setState("3");
		homeUser.setHomeId(Long.parseLong(homeId));
		homeUser.setUserName(username);
		
		boolean isSend = VWWebSocket.cloudSyncSendUser(homeUser);
		int line = homeUserDAO.exitHome(homeId, username, isSend);
		
		if (line == 1) {
			json.put("result", 1);
			json.put("message", "成功离开家庭");
		} else {
			json.put("result", 0);
			json.put("message", "没找到家庭");
		}
		String jsonString = json.toString();
		sendMessage(jsonString, response);
	}

	private void doInviteHomeUser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject json = new JSONObject();
		// 添加家人（邀请加入家庭）
		String type = request.getParameter("type");
		String inviter = request.getParameter("inviter");
		String owner = request.getParameter("owner");
		String homeId = request.getParameter("VMHomeId");
		long id = Long.parseLong(homeId);
		VMHomeUser temp = homeUserDAO.get(id, owner);
		System.out.println(temp);
		if (!TCUtil.isMobileNO(owner) || !TCUtil.isMobileNO(inviter) || temp == null) {
			json.put("result", 0);
			json.put("message", "输入信息不正确！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}
		// 如果邀请用户是新用户，发短信给inviter用户，提醒有人邀请他加入家庭
		// 这里依赖淘宝
		if (userDAO.get(inviter) == null) {
			String params = "{\"name\":\"" + owner + "\"}";
			String message = TCUtil.TaobaoClientSMS(inviter, "雨蛙智能", params, "SMS_68590049");
			if (message != null && !message.isEmpty()) {
				json.put("result", 0);
				json.put("message", message);
				String jsonString = json.toString();
				sendMessage(jsonString, response);
				return;
			}
		}

		VWHomeNotice notice = noticeDAO.get(type, inviter, Long.parseLong(homeId));
		if (notice != null) {
			json.put("result", 0);
			json.put("message", "重复邀请加入家庭！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}

		notice = new VWHomeNotice();
		notice.setNoticeType(type);
		notice.setInviter(inviter);
		notice.setOwner(owner);
		notice.setHomeId(Long.parseLong(homeId));
		notice.setNoticeTime(TCUtil.getNowTimeString());

		// 测试可以自增的
		int line = noticeDAO.save(notice);
		if (line == 1) {
			// 查出ID出来
			notice = noticeDAO.get(type, inviter, Long.parseLong(homeId));

			// 这里要增加显示主人的名字
			VMUser ownerUser = userDAO.get(owner);
			notice.setOwnerNickname(ownerUser.getNickName());

			json.put("result", 1);
			json.put("message", "成功邀请");

			// 向在线用户发送通知(这个包括手机用户 利用WS发送回去)
			JSONObject msgJson = new JSONObject();
			msgJson.put("type", "HomeNotice");
			msgJson.put("notice", notice);
			VWWebSocket.sendAppClientUserMessage(inviter, msgJson.toJSONString());

			// 比如 : {"noticeId":123, "noticeType":"InviteHomeUser",
			// "owner":"138……", "noticeTime":""}
			System.out.println("向被邀请用户发送通知WS ==== " + msgJson.toJSONString());



			// TODO: 1上线前要变true
			String pushMessage = "'"+ ownerUser.getNickName() + "(" + owner+")'"+  "邀请你加入他的家庭";
			System.out.println(pushMessage);
			JPushUtil.PUST(inviter,pushMessage,false);


			// 将用户信息写入VMHomeUser
			VMHomeUser homeUser = new VMHomeUser();
			homeUser.setNickName(request.getParameter("nickName"));
			homeUser.setMyRoomIds(request.getParameter("myRoomIds"));
			homeUser.setRelate(request.getParameter("relate"));
			homeUser.setRemarks(request.getParameter("remarks"));
			homeUser.setIsControls(request.getParameter("isControls"));
			homeUser.setHomeId(Long.parseLong(homeId));
			homeUser.setUserName(inviter);
			homeUser.setState("1");
			line = homeUserDAO.update(homeUser);
			if (line != 1) {
				homeUserDAO.save(homeUser);
			}

		} else {
			json.put("result", 0);
			json.put("message", "出错");
		}
		String jsonString = json.toString();
		sendMessage(jsonString, response);
		return;
	}

	private void doDealInvite(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject json = new JSONObject();
		long noticeId = Long.parseLong(request.getParameter("noticeId"));
		VWHomeNotice notice = noticeDAO.get(noticeId);
		if (notice == null) {
			json.put("result", 0);
			json.put("message", "未找到该通知！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}

		String type = request.getParameter("type");
		String agree = request.getParameter("agree");

		// Boolean.parseBoolean
		if (Integer.parseInt(agree) == 1) {
			// 注意 : 这里notice中的数据是一定正确的 因为写入的时候验证过
			// 写数据库, 如果家庭在线，发送更新用户信息
			VMHomeUser user = homeUserDAO.getSyncUserMessage(notice.getHomeId(), notice.getInviter());

			JSONObject msgJson = new JSONObject();
			msgJson.put("type", "SendMessage");
			msgJson.put("data", user);
			System.out.println("WS发送信息给Panel ==== 401" + msgJson.toJSONString());
			
			Boolean isSend = VWWebSocket.cloudSyncSendUser(user);
			if (!isSend) {
				// TODO ServerLogin 下次服务器登录的时候需要处理
				user.setState("2");
				homeUserDAO.update(user);
			} else {
				user.setState("0");
				homeUserDAO.update(user);
			}
		} else { // 拒绝 (拒绝本来就应该直接删除)
			homeUserDAO.delete(notice.getHomeId(), notice.getInviter());
		}

		// 注意 : 以下是通过WS回去的
		// 生成通知，邀请人已接受/拒绝邀请，并向房主发送
		VWHomeNotice dealNotice = new VWHomeNotice();
		dealNotice.setNoticeType(type);
		dealNotice.setOwner(notice.getOwner());
		dealNotice.setInviter(notice.getInviter());
		dealNotice.setAgree(agree);
		dealNotice.setNoticeTime(TCUtil.getNowTimeString());

		int line = noticeDAO.save(dealNotice);
		if (line == 1) {
			// 查出ID来
			dealNotice = noticeDAO.get(dealNotice.getNoticeTime());
			// 这里要增加显示被邀请人的名字
			VMUser ownerUser = userDAO.get(notice.getInviter());
			dealNotice.setInviterNickname(ownerUser.getNickName());

			json.put("result", 1);
			json.put("message", "已经同意");
			// 向在线用户发送通知
			JSONObject msgJson = new JSONObject();
			msgJson.put("type", "HomeNotice");
			msgJson.put("notice", dealNotice);
			System.out.println("向邀请者用户发送通知WS ==== " + msgJson.toJSONString());


			// TODO: 1上线前要变true
			String agreeMessage = Integer.parseInt(agree) == 1 ? "接受" : "拒绝";
			String pushMessage = "'"+ dealNotice.getInviterNickname() + "(" + dealNotice.getInviter()+")'"+ agreeMessage + "加入你的家庭";
			System.out.println(pushMessage);
			JPushUtil.PUST(dealNotice.getOwner(),pushMessage,false);

			
			VWWebSocket.sendAppClientUserMessage(notice.getOwner(), msgJson.toJSONString());
		} else {
			json.put("result", 0);
			json.put("message", "系统内部错误");
		}
		noticeDAO.delete(noticeId);
		String jsonString = json.toString();
		sendMessage(jsonString, response);
	}

	private void doApplyHomeUser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject json = new JSONObject();
		String type = request.getParameter("type");
		String applyer = request.getParameter("applyer");
		String owner = request.getParameter("owner");
		String homeId = request.getParameter("VMHomeId");
		if (!TCUtil.isMobileNO(owner) || !TCUtil.isMobileNO(applyer)
				|| (homeId != null && homeUserDAO.get(Long.parseLong(homeId), owner) == null)
				|| homeUserDAO.getUsernameList(owner).size() <= 0) {
			json.put("result", 0);
			json.put("message", "输入信息不正确！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}

		VWHomeNotice notice = noticeDAO.get(type, applyer, owner);
		if (notice != null) {
			json.put("result", 0);
			json.put("message", "重复申请加入家庭！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}

		notice = new VWHomeNotice();
		notice.setApplyer(applyer);
		notice.setOwner(owner);
		notice.setNoticeType(type);
		notice.setNoticeTime(TCUtil.getNowTimeString());

		if (homeId != null) {
			notice.setHomeId(Long.parseLong(homeId));
		}

		int line = noticeDAO.save(notice);
		if (line == 1) {
			// 查出ID来
			notice = noticeDAO.get(notice.getNoticeTime());

			// 这里要增加显示被邀请人的名字
			VMUser Applyer = userDAO.get(notice.getApplyer());
			notice.setApplyerNickname(Applyer.getNickName());

			json.put("result", 1);
			json.put("message", "申请成功");
			// 向在线用户发送通知
			JSONObject msgJson = new JSONObject();
			msgJson.put("type", "HomeNotice");
			msgJson.put("notice", notice);


			// TODO: 1上线前要变true
			String pushMessage = "'"+ Applyer.getNickName() + "(" + applyer+")'"+  "申请加入你的家庭";
			System.out.println(pushMessage);
			JPushUtil.PUST(owner,pushMessage,false);


			
			VWWebSocket.sendAppClientUserMessage(owner, msgJson.toString());
			System.out.println("向主人用户发送通知WS(申请) ==== " + msgJson.toJSONString());

		} else {
			json.put("result", 0);
			json.put("message", "数据库出现问题！");
		}

		String jsonString = json.toString();
		sendMessage(jsonString, response);

	}

	private void doDealApply(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject json = new JSONObject();
		String type = request.getParameter("type");
		long noticeId = Long.parseLong(request.getParameter("noticeId"));

		String agree = request.getParameter("agree");

		VWHomeNotice notice = noticeDAO.get(noticeId);
		if (notice == null) {
			json.put("result", 0);
			json.put("message", "没有找到该信息！");
			String jsonString = json.toString();
			sendMessage(jsonString, response);
			return;
		}

		if (Integer.parseInt(agree) == 1) {
			String homeId = request.getParameter("VMHomeId");
			if (homeId == null || homeId.isEmpty() || homeDAO.get(Long.parseLong(homeId)) == null) {
				json.put("result", 0);
				json.put("message", "没有家庭信息！");
				String jsonString = json.toString();
				sendMessage(jsonString, response);
				return;
			}
			if (notice.getHomeId() == 0) {
				notice.setHomeId(Long.parseLong(homeId));
			}
			
			// 写数据库, 如果家庭在线，发送更新用户信息 用户信息写入VMHomeUser(这个是用来选择家庭用 username 和
			// homeID)
			String nickName = request.getParameter("nickName");
			String myRoomIds = request.getParameter("myRoomIds");
			String relate = request.getParameter("relate");
			String remarks = request.getParameter("remarks");
			String isControls = request.getParameter("isControls");

			// 这个是联表查的
			VMHomeUser user = homeUserDAO.getSyncUserMessage(notice.getHomeId(), notice.getApplyer());
			if (user == null) {
				user = new VMHomeUser();
				user.setHomeId(notice.getHomeId());
				user.setUserName(notice.getApplyer());
				homeUserDAO.save(user);
			}
			user.setNickName(nickName);
			user.setMyRoomIds(myRoomIds);
			user.setRelate(relate);
			user.setRemarks(remarks);
			user.setIsControls(isControls);
			user.setState("2");
			
			VMUser password = userDAO.get(notice.getApplyer());
			user.setPassword(password.getPassword());
			
			System.out.println("向panel发送同步用户WS ==== " + user);
			
			boolean isSend = VWWebSocket.cloudSyncSendUser(user);
			if (!isSend) {
				user.setState("2");
				homeUserDAO.update(user);
			} else {
				user.setState("0");
				homeUserDAO.update(user);
			}
		} else {// 拒绝
			
		}

		// 生成通知，申请已被接受/拒绝，并向申请人发送
		VWHomeNotice dealNotice = new VWHomeNotice();
		dealNotice.setNoticeType(type);
		dealNotice.setOwner(notice.getOwner());
		dealNotice.setApplyer(notice.getApplyer());
		dealNotice.setAgree(agree);
		dealNotice.setNoticeTime(TCUtil.getNowTimeString());

		int line = noticeDAO.save(dealNotice);
		if (line == 1) {
			// 查出ID来
			dealNotice = noticeDAO.get(dealNotice.getNoticeTime());
			// 这里要增加显示被邀请人的名字
			VMUser ownerUser = userDAO.get(notice.getOwner());
			dealNotice.setOwnerNickname(ownerUser.getNickName());

			json.put("result", 1);
			json.put("message", "已经同意");
			// 向在线用户发送通知
			JSONObject msgJson = new JSONObject();
			msgJson.put("type", "HomeNotice");
			msgJson.put("notice", dealNotice);
			System.out.println("向在线APP 申请者用户发送通知WS ==== " + msgJson.toJSONString());

			// TODO: 1上线前要变true
			String agreeMessage = Integer.parseInt(agree) == 1 ? "接受" : "拒绝";
			String pushMessage = "'"+ dealNotice.getOwnerNickname() + "(" + dealNotice.getOwner()+")'"+ agreeMessage + "你加入他的家庭";
			System.out.println(pushMessage);
			JPushUtil.PUST(dealNotice.getApplyer(),pushMessage,false);
			
			VWWebSocket.sendAppClientUserMessage(notice.getApplyer(), msgJson.toJSONString());
		} else {
			json.put("result", 0);
			json.put("message", "系统内部错误");
		}
		noticeDAO.delete(noticeId);
		String jsonString = json.toString();
		sendMessage(jsonString, response);
	}

	private void sendMessage(String jsonString, HttpServletResponse response) throws IOException {
		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}

}
