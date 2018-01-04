package com.veewap.app.servlet;

import com.veewap.dao.IVMUserDAO;
import com.veewap.dao.impl.VMUserDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


//邀请家人模块(HTTP)
@WebServlet("/notice")
public class VWHomeNoticeServlet extends HttpServlet {

    // DAO只初始化一次
    private IVMUserDAO userDAO = new VMUserDAOImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

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


	}

	private void sendMessage(String jsonString, HttpServletResponse response) throws IOException {
		PrintWriter pw = response.getWriter();
		pw.write(jsonString);
		pw.flush();
		pw.close();
	}

}
