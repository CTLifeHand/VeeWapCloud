package com.veewap.app.servlet;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.veewap.dao.IVMUserDAO;
import com.veewap.dao.impl.VMUserDAOImpl;
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
import java.util.stream.Collectors;


@WebServlet("/VWPushServlet")
public class VWPushServlet extends HttpServlet {

    // DAO只初始化一次
    private IVMUserDAO userDAO = new VMUserDAOImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html;charset=utf-8");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers","Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");


		Map<String, Object> objectMap = TCUtil.mapToHashMap(request.getParameterMap());
		JSONObject paras = new JSONObject(objectMap);
		System.out.println("VWHomeServlet====" + paras.toJSONString());

		String alias = request.getParameter("alias");
		if (alias!=null &&(!TCUtil.isMobileNO(alias) || userDAO.get(alias) == null)) {
			response.sendError(400);
			return;
		}

		String content = request.getParameter("content");
		Boolean isProduction = Boolean.parseBoolean(request.getParameter("isProduction"));
		String page = request.getParameter("page");

		if (content == null){
			response.sendError(400);
			return;
		}


		if (page!=null){
			JsonObject extra = new JsonObject();
			extra.addProperty("page",page);
		}else {
			JPushUtil.PUST(alias,content,isProduction,null);
		}
		JSONObject json = new JSONObject();
		json.put("success", 1);
		String jsonString = json.toString();
		sendMessage(jsonString, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html;charset=utf-8");
        response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers","Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

		Map<String, Object> objectMap = TCUtil.mapToHashMap(request.getParameterMap());
		JSONObject paras = new JSONObject(objectMap);
		System.out.println("VWPushServlet 的URL参数 ====" + paras.toJSONString());

		String pushJson = request.getReader().lines().collect(Collectors.joining());

		JsonObject push = new JsonParser().parse(pushJson).getAsJsonObject();
		String alias = push.get("alias").getAsString();

		if (alias!=null &&(!TCUtil.isMobileNO(alias) || userDAO.get(alias) == null)) {
			response.sendError(400);
			return;
		}

		String content = push.get("content").getAsString();
		Boolean isProduction = push.get("isProduction").getAsBoolean();
		JsonObject extra = push.get("extra").getAsJsonObject();

		if (content == null){
			response.sendError(400);
			return;
		}


		if (extra!=null){
			JPushUtil.PUST(alias,content,isProduction,extra);
		}else {
			JPushUtil.PUST(alias,content,isProduction,null);
		}

//		JSONObject json = new JSONObject();
//		json.put("success", 1);
		String jsonString = push.toString();
		sendMessage(jsonString, response);
	}

	private void sendMessage(String jsonString, HttpServletResponse response) throws IOException {
		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}
}
