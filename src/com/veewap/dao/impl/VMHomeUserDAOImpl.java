package com.veewap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.veewap.dao.IVMHomeUserDAO;
import com.veewap.domain.VMHomeUser;
import com.veewap.template.IResultSetHandler;
import com.veewap.template.JdbcTemplate;


public class VMHomeUserDAOImpl implements IVMHomeUserDAO {

	@Override
	public int save(VMHomeUser home) {
		String sql = "INSERT INTO VMHomeUser (homeId,userName,permission,state,myRoomIds,level,relate,remarks,isControls,icon) VALUES(?,?,?,?,?,?,?,?,?,?)";

		Object[] params = { home.getHomeId(), home.getUserName(), home.getPermission(), home.getState(),
				home.getMyRoomIds(), home.getLevel(), home.getRelate(), home.getRemarks(), home.getIsControls(),
				home.getIcon() };
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public int update(VMHomeUser home) {
		String sql = "UPDATE VMHomeUser SET permission=?,state=?,myRoomIds=?,level=?,relate=?,remarks=?,isControls=?,icon=? WHERE homeId = ? and userName=?";
		Object[] params = { home.getPermission(), home.getState(), home.getMyRoomIds(), home.getLevel(),
				home.getRelate(), home.getRemarks(), home.getIsControls(), home.getIcon(), home.getHomeId(),
				home.getUserName() };
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public void delete(Long homeId) {
		String sql = "DELETE FROM VMHomeUser WHERE homeId = ?";
		JdbcTemplate.update(sql, homeId);
	}

	@Override
	public void delete(Long homeId, String name) {
		String sql = "DELETE FROM VMHomeUser WHERE homeId = ? and userName=?";
		JdbcTemplate.update(sql, homeId, name);
	}

	@Override
	public VMHomeUser get(Long id, String name) {
		String sql = "SELECT * FROM VMHomeUser WHERE homeId = ? and userName=?";
		List<VMHomeUser> list = JdbcTemplate.query(sql, new VMHomeUserResultSetHanlder(), id, name);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<VMHomeUser> list() {
		String sql = "SELECT * FROM VMHomeUser";
		return JdbcTemplate.query(sql, new VMHomeUserResultSetHanlder());
	}
	

	public List<VMHomeUser> getUsernameList(String userName) {
		String sql = "SELECT * FROM VMHomeUser WHERE userName=?";
		return JdbcTemplate.query(sql, new VMHomeUserResultSetHanlder(),userName);
	}

	class VMHomeUserResultSetHanlder implements IResultSetHandler<List<VMHomeUser>> {
		@Override
		public List<VMHomeUser> handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<VMHomeUser> list = new ArrayList<>();
			try {
				while (rs.next()) {
					VMHomeUser home = new VMHomeUser();
					list.add(home);
					home.setHomeId(rs.getLong("homeId"));
					home.setUserName(rs.getString("userName"));
					home.setPermission(rs.getString("permission"));
					home.setState(rs.getString("state"));
					home.setMyRoomIds(rs.getString("myRoomIds"));
					home.setLevel(rs.getString("level"));
					home.setRelate(rs.getString("relate"));
					home.setRemarks(rs.getString("remarks"));
					home.setIsControls(rs.getString("isControls"));
					home.setIcon(rs.getString("icon"));
					home.setPassword(rs.getString("password"));
				}
			} catch (SQLException e) {
				// TODO: handle exception
				System.out.println(e);
				System.out.println(rs);
			}
			return list;
		}

	}

	public int exitHome(String homeId, String username, boolean isOnline) {
		String sql = "delete from vmhomeuser where homeid=? and username=?";
		if (!isOnline) {
			sql = "update vmhomeuser set state=3 where homeid=? and username=?";
		}
		return JdbcTemplate.update(sql, homeId, username);
	}

	@Override
	public VMHomeUser getSyncUserMessage(Long homeid, String inviter) {
//		String sql = "select vmhomeuser.username as username,state,password,vmhomeuser.nickname as nickname,myroomids,relate,remarks,iscontrols"
		String sql = "select *,password"
				+ " from vmhomeuser" + " left join vmuser on vmhomeuser.username=vmuser.username"
				+ " where homeid=? and vmhomeuser.username=?";
		
		List<VMHomeUser> list = JdbcTemplate.query(sql, new VMHomeUserResultSetHanlder(), homeid, inviter);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * @author CYT
	 *	对应老代码的syncUserMessage 
	 */
	@Override 
	public List<VMHomeUser> getListUserMessage(Long homeid) {
//		String sql = "select vmhomeuser.username as username,state,password,vmhomeuser.nickname as nickname,myroomids,relate,remarks,iscontrols"
		String sql = "select *,password"
				+ " from vmhomeuser" + " left join vmuser on vmhomeuser.username=vmuser.username"
				+ " where homeid=?";
		return JdbcTemplate.query(sql, new VMHomeUserResultSetHanlder(), homeid);
	}
	
	

}
