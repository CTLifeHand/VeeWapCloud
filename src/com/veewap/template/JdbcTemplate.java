package com.veewap.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.veewap.util.JdbcUtil;


public class JdbcTemplate {
	
	// 工具类
	private JdbcTemplate() {
	}
	
	public static int update(String sql , Object... params){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
//			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CoderTonyChan","root","asdfgh");
			conn = JdbcUtil.getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				Object object = params[i];
				ps.setObject(i+1, object);
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn, ps, null);
		}
		return 0;
	}

	public static <T> T query(String sql, IResultSetHandler<T> resultSet,Object... params) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = JdbcUtil.getConn();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				Object object = params[i];
				ps.setObject(i+1, object);
			}
			rs = ps.executeQuery();
			return resultSet.handle(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn, ps, null);
		}
		return null;
	}
	
	
}
