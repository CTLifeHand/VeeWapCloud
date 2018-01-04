package com.veewap.dao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.veewap.dao.IVMHomeDAO;
import com.veewap.domain.VMHome;
import com.veewap.template.IResultSetHandler;
import com.veewap.template.JdbcTemplate;
import com.veewap.util.TCUtil;

public class VMHomeDAOImpl implements IVMHomeDAO {

	@Override
	public int save(VMHome home) {
		String sql = "INSERT INTO vmhome (id,homeName,city,homeAddress,homeMessage,isOnline,offlineTime,serverPanelId,isVisual,isDelete) VALUES(?,?,?,?,?,?,?,?,?,?)";

		Object[] params = { home.getId(), home.getHomeName(), home.getCity(), home.getHomeAddress(),
				home.getHomeMessage(), home.getIsOnline(), home.getOfflineTime(), home.getServerPanelId(),
				home.getIsVisual(), home.getIsDelete() };
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public int update(VMHome home) {
		String sql = "UPDATE vmhome SET homeName=?,city=?,homeAddress=?,homeMessage=?,isOnline=?,offlineTime=?,serverPanelId=?,isVisual=?,isDelete=? WHERE id = ?";
		Object[] params = { home.getHomeName(), home.getCity(), home.getHomeAddress(), home.getHomeMessage(),
				home.getIsOnline(), home.getOfflineTime(), home.getServerPanelId(), home.getIsVisual(),
				home.getIsDelete(), home.getId() };
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public int delete(Long id) {
		String sql = "DELETE FROM VMHome WHERE id = ?";
		return JdbcTemplate.update(sql, id);
	}

	@Override
	public VMHome get(Long id) {
		String sql = "SELECT * FROM VMHome WHERE id = ?";
		List<VMHome> list = JdbcTemplate.query(sql, new VMHomeResultSetHanlder(), id);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<VMHome> list() {
		String sql = "SELECT * FROM VMHome";
		return JdbcTemplate.query(sql, new VMHomeResultSetHanlder());
	}

	class VMHomeResultSetHanlder implements IResultSetHandler<List<VMHome>> {
		@Override
		public List<VMHome> handle(ResultSet rs) throws SQLException {
			List<VMHome> list = new ArrayList<>();
			while (rs.next()) {
				VMHome home = new VMHome();
				list.add(home);
				home.setId(rs.getLong("id"));
				home.setHomeName(rs.getString("homeName"));
				home.setCity(rs.getString("city"));
				home.setHomeAddress(rs.getString("homeAddress"));
				home.setHomeMessage(rs.getString("homeMessage"));
				home.setIsOnline(rs.getString("isOnline"));
				home.setOfflineTime(rs.getString("offlineTime"));
				home.setServerPanelId(rs.getInt("serverPanelId"));
				home.setIsVisual(rs.getInt("isVisual"));
				home.setIsDelete(rs.getInt("isDelete"));
			}
			return list;
		}

	}

	public List<VMHome> getHomeArray(int offset, int length, int isOnline) {
		String sql;
		// 注意 : 这里也可以变好看一些 参考 Feedback
		// 是否加Limit
		if (offset < 1 && length < 1) { // 无限制
			sql = "select * from vmhome"
					+ (isOnline > -1 ? (" where isonline=" + isOnline + " and isvisual=0") : " where isvisual=0");
		} else {
			offset = offset < 1 ? 0 : offset;
			// length = offset + length > totalHomeCount ? totalHomeCount -
			// offset : length; // 超出也没问题吧?
			sql = "select * from vmhome"
					+ (isOnline > -1 ? (" where isonline=" + isOnline + " and isvisual=0") : " where isvisual=0")
					+ " LIMIT " + offset + "," + length;
		}
		System.out.println(sql);
		return JdbcTemplate.query(sql, new VMHomeResultSetHanlder());
	}

	/**
	 * @author CYT 注意 : 这里并没有开启事务 也没有回滚
	 */
	public int deleteVMHome(Long id) {
		String sql = "delete from vmhomeuser where homeid=?";
		JdbcTemplate.update(sql, id);
		sql = "update vmhome set isdelete=1 where id=?";
		int line = JdbcTemplate.update(sql, id);
		sql = "delete from vmhome where isvisual=1 and id=?";
		JdbcTemplate.update(sql, id);
		return line;
	}

	public void offlineHome(long homeId) {
		VMHome home = get(homeId);
		home.setOfflineTime(TCUtil.getNowTimeString());
		save(home);
	}

	@Override
	public void setVisual(long vMHomeId) {
		String sql = "update vmhome set isvisual=1 where id=?";
		JdbcTemplate.update(sql, vMHomeId);
	}

	@Override
	public void saveHomeMessage(long vMHomeId, String homeMessage) {
		// 保存文件
		String homeMessagePath = "/" + vMHomeId + "/homeMessage.json";
		try {
			String path = TCUtil.getDefaultPath();
			File file = new File(path + homeMessagePath);
			file.deleteOnExit();
			file.getParentFile().mkdirs();
			FileOutputStream outputStream = new FileOutputStream(file);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			bufferedWriter.write(homeMessage);
			bufferedWriter.flush();
			bufferedWriter.close();
			outputStream.close();

			String sql = "update vmhome set homemessage='" + homeMessagePath + "' where id=" + vMHomeId;
			JdbcTemplate.update(sql);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<VMHome> getHomeServers(String username) {
		String sql = "select vmhome.id as VMHomeId,vmhome.homename as VMHomeName, vmhome.city as VMHomeCity,vmhome.homeaddress as VMHomeAddress,isOnline,isVisual "
				+ " from vmhomeuser"
				+ " left join vmhome on vmhomeuser.homeid=vmhome.id"
				+ " where vmhomeuser.state=0 and vmhomeuser.username=?";
		return JdbcTemplate.query(sql, new VMHomeServersSetHanlder(),username);
	}
	
	class VMHomeServersSetHanlder implements IResultSetHandler<List<VMHome>> {
		@Override
		public List<VMHome> handle(ResultSet rs) throws SQLException {
			List<VMHome> list = new ArrayList<>();
			while (rs.next()) {
				VMHome home = new VMHome();
				list.add(home);
				home.setVMHomeId(rs.getLong("VMHomeId"));
				home.setVMHomeName(rs.getString("VMHomeName"));
				home.setVMHomeCity(rs.getString("VMHomeCity"));
				home.setVMCity(rs.getString("VMHomeCity"));
				home.setVMHomeAddress(rs.getString("VMHomeAddress"));
				home.setIsOnline(rs.getString("isOnline"));
				home.setIsVisual(rs.getInt("isVisual"));
				home.setIsConnected(rs.getString("isOnline"));
			}
			return list;
		}

	}
}
