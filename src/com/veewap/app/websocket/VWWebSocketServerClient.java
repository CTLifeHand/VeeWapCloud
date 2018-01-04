package com.veewap.app.websocket;

import javax.websocket.Session;


import com.alibaba.fastjson.JSONArray;

public class VWWebSocketServerClient extends VWWebSocketClient {


	
	//下面几个变量是服务器信息，仅服务器连接到云端才有数据
	public String VMHomeName = "";
	public String VMCity = "";
	public String VMHomeAddress = "";
	public int ServerPanelId = -1;
	public String VMWeather = "";
	public JSONArray userArray = null;
	
	
	
	public VWWebSocketServerClient(Session session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

}
