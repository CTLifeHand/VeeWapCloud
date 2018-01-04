package com.veewap.dao.impl;

import com.veewap.dao.IVMUserDAO;
import com.veewap.domain.VMUser;
import com.veewap.template.IResultSetHandler;
import com.veewap.template.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class VMUserDAOImpl implements IVMUserDAO {

	@Override
	public void save(VMUser home) {
		if (home.getId() == 0 ) {
			// 这个表格是自动增长
			String sql = "INSERT INTO VMUser (userName,nickName,password,power) VALUES(?,?,?,?,?)";
			Object[] params = {home.getUserName(), home.getNickName(), home.getPassword(), home.getPower() };
			JdbcTemplate.update(sql, params);
		}else{
			String sql = "INSERT INTO VMUser (id,userName,nickName,password,power) VALUES(?,?,?,?,?,?)";
			Object[] params = { home.getId(), home.getUserName(), home.getNickName(), home.getPassword(), home.getPower() };
			JdbcTemplate.update(sql, params);
		}
	}

	@Override
	public void update(VMUser home) {
		String sql = "UPDATE VMUser SET userName=?,nickName=?,password=?,power=? WHERE id = ?";
		Object[] params = { home.getUserName(), home.getNickName(), home.getPassword(), home.getPower() ,home.getId()};
		JdbcTemplate.update(sql, params);
	}

	@Override
	public void delete(Long id) {
		String sql = "DELETE FROM VMUser WHERE id = ?";
		JdbcTemplate.update(sql, id);
	}

	@Override
	public VMUser get(Long id) {
		String sql = "SELECT * FROM VMUser WHERE id = ?";
		List<VMUser> list = JdbcTemplate.query(sql, new VMUserResultSetHanlder(), id);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public VMUser get(String username) {
		String sql = "SELECT * FROM VMUser WHERE username = ?";
		List<VMUser> list = JdbcTemplate.query(sql, new VMUserResultSetHanlder(), username);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<VMUser> list() {
		String sql = "SELECT * FROM VMUser";
		return JdbcTemplate.query(sql, new VMUserResultSetHanlder());
	}

	class VMUserResultSetHanlder implements IResultSetHandler<List<VMUser>> {
		@Override
		public List<VMUser> handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<VMUser> list = new ArrayList<>();
			while (rs.next()) {
				VMUser home = new VMUser();
				list.add(home);
				home.setId(rs.getLong("id"));
				home.setUserName(rs.getString("userName"));
				home.setNickName(rs.getString("nickName"));
				home.setPassword(rs.getString("password"));
				home.setIcon(rs.getString("icon"));
				home.setPower(rs.getString("power"));
			}
			return list;
		}

	}
	

	class VMUserResultCountSetHanlder implements IResultSetHandler<Integer> {
		@Override
		public Integer handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			while (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		}
	}
	
	public boolean checkAdminLogin(String username, String password) {
		String sql = "select count(*) from VMUser where username='"+username+"' and password='"+password+"' and power > 15";

		return JdbcTemplate.query(sql, new VMUserResultCountSetHanlder()).intValue() == 1? true:false;
	}
}
