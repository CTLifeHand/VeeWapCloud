package com.veewap.app.websocket;

import com.alibaba.fastjson.JSONArray;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;



public class VWWebSocketClient {
	//TODO: 公共信息 (使用子类来做 开始的时候很难判断)
	public boolean isClientServer = false;
	// 该服务器上次通讯的时间
	public long lastMessageTime;
	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	protected Session session;
	
	public long VMHomeId = -1; //负一表示还没初始化完成，或者误码
	public String VMHomeName = "";
	
	//TODO: 服务器信息 (使用子类来做 开始的时候很难判断)
	public String VMCity = "";
	public String VMHomeAddress = "";
	public int ServerPanelId = -1;
	public String VMWeather = "";
	public JSONArray userArray = null;


	//TODO: App客户端信息 (使用子类来做 开始的时候很难判断)
	public String username = "";
	
	
	public VWWebSocketClient(Session session) {
		this.session = session;
		this.lastMessageTime = new Date().getTime();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message){
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 虽说是异步，不过高频率发送并没有出现错乱的情况，还有待研究。
		// this.session.getAsyncRemote().sendText(message);
	}
	
	
	public void close(){
        try {
        	session.close();
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
