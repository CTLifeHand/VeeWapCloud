package com.veewap.web.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.veewap.app.websocket.VWWebSocket;
import com.veewap.dao.IVMHomeDAO;
import com.veewap.dao.IVMUserDAO;
import com.veewap.dao.IVWFeedbackDAO;
import com.veewap.dao.impl.VMHomeDAOImpl;
import com.veewap.dao.impl.VMUserDAOImpl;
import com.veewap.dao.impl.VWFeedbackDAOImpl;
import com.veewap.domain.VMHome;
import com.veewap.domain.VWFeedback;
import com.veewap.util.TCUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@WebServlet("/VWHomeManagerServlet")
public class VWHomeManagerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// DAO只初始化一次
	private IVMUserDAO userDAO = new VMUserDAOImpl();
	private IVMHomeDAO homeDAO = new VMHomeDAOImpl();
	private IVWFeedbackDAO feedbackDAO = new VWFeedbackDAOImpl();

	private BufferedReader bufferedReader;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		super.doGet(request, response);
		doPost(request, response);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		super.doPost(request, response);
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		response.setHeader("Access-Control-Allow-Origin", "*"); //设置post请求允许跨域

//		resp.setHeader("P3P","CP=CAO PSA OUR"); //设置静态页面的不同请求可以获取到Session，TODO 需验证？？？
		response.setHeader("P3P","CP=IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT");
		
		String type = request.getParameter("type");
//		System.out.println(type);
		if(type == null) return;
		
		if(type.equals("login")){
			System.out.println(type);
			doLogin(request, response);
		}else if (type.equals("getHomeArray")){
			System.out.println(type);
			doGetHomeArray(request, response);
		}else if (type.equals("getHomeMessage")){
			System.out.println(type);
			doGetHomeMessage(request, response);
		}else if (type.equals("setServerPanel")){
			System.out.println(type);
			doSetServerPanel(request, response);
		}else if (type.equals("setHomeManager")){
			System.out.println(type);
			doSetHomeManager(request, response);
		}else if (type.equals("getFeedback")){
			System.out.println(type);
			doGetFeedback(request, response);
		}
		
	}
	
	private void doLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		json.put("type", "login");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if(userDAO.checkAdminLogin(username, password)){
			json.put("username", username);
			json.put("success", 1);
			//添加session
			request.getSession().setAttribute("username", username);
		}
		else{
			json.put("success", 0);
		}
		
		String jsonString = json.toString();
		System.out.println(jsonString);

		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}
	
	private void doGetHomeArray(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		json.put("type", "getHomeArray");

		int offset = request.getParameter("offset") != null ? Integer.parseInt(request.getParameter("offset")) : 0;
		int length = request.getParameter("length") != null ? Integer.parseInt(request.getParameter("length")) : -1;
		int isOnline = request.getParameter("isOnline") != null ? Integer.parseInt(request.getParameter("isOnline")) : -1;
		List<VMHome> list = homeDAO.getHomeArray(offset, length, isOnline);
		
		json.put("VMHomeArray", list);
		json.put("totalHomeCount", list.size());
		json.put("success", 1);
		
		// 注意 因为命名不规范 导致会首字母小写
		String jsonString = json.toString();
//		System.out.println(jsonString);

		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}
	
	
	/**
	 * @author CYT
	 *	json是在线家庭每5min更新一次
	 */
	private void doGetHomeMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取指定家庭服务器信息
		JSONObject json = new JSONObject();
		json.put("type", "getHomeMessage");
		
		long homeId = Long.parseLong(request.getParameter("VMHomeId"));
		VMHome home = homeDAO.get(homeId);
		if (home != null) {
			json.put("VMHomeMessage", home);
			json.put("success", 1);
		}else{
			json.put("success", 0);
			json.put("message", "没有该用户");
		}
		if (home.getHomeMessage() != null) {
			String homeMessagePath = TCUtil.getDefaultPath() + home.getHomeMessage();
			try {
				FileInputStream inputStream = new FileInputStream(new File(homeMessagePath));
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				String homeMessage = bufferedReader.readLine();
				JSONObject homeMessageJson = JSON.parseObject(homeMessage);
				home.setVMHomeMessage(homeMessageJson);
			} catch (Exception e) {
				System.out.println(e);
			}
			home.setHomeMessage(null);
		}
		
		String jsonString = json.toString();
		System.out.println(jsonString);

		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}
	
	private void doSetServerPanel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//注意：此消息发送成功不表示切换主控成功，客户端需提示"正在尝试设置家庭主控，请稍后刷新查看结果"
		JSONObject json = new JSONObject();
		json.put("type", "setServerPanel");
		
		long homeId = Long.parseLong(request.getParameter("VMHomeId"));
		int serverPanelId = Integer.parseInt(request.getParameter("ServerPanelId"));
		boolean success = VWWebSocket.changeServerPanel(homeId, serverPanelId);
		json.put("success", success ? 1 : 0); 

		String jsonString = json.toString();
		System.out.println(jsonString);

		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}
	
	private void doSetHomeManager(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//设置家庭管理员
		//注意：此消息发送成功不表示切换主控成功，客户端需提示"正在尝试设置家庭管理员，请稍后刷新查看结果"
		JSONObject json = new JSONObject();
		json.put("type", "setHomeManager");
		
		long homeId = Long.parseLong(request.getParameter("VMHomeId"));
		int userId = Integer.parseInt(request.getParameter("UserId"));
		
		boolean success = VWWebSocket.changeHomeManager(homeId, userId);
		
		json.put("success", success ? 1 : 0);
		String jsonString = json.toString();
		System.out.println(jsonString);
		
		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}
	
	/**
		offset、length: 同获取家庭列表
    	timeLimit：时间限制，单位：天，如仅显示30天内的反馈信息
	 */
	private void doGetFeedback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		json.put("type", "getFeedback");
		
		int offset = request.getParameter("offset") != null ? Integer.parseInt(request.getParameter("offset")) : 0;
		long length = request.getParameter("length") != null ? Long.parseLong(request.getParameter("length")) : -1;
		int timeLimit = request.getParameter("timeLimit") != null ? Integer.parseInt(request.getParameter("timeLimit")) : -1;
		List<VWFeedback> list = feedbackDAO.getFeedbackArray(offset, length, timeLimit);
		json.put("VMFeedbackArray", list);
		json.put("totalFeedbackCount", list.size());
		json.put("success", 1);
		
		String jsonString = json.toString();
		
//		System.out.println(jsonString);

		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}
}
