package com.veewap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.veewap.dao.IVWVersionDAO;
import com.veewap.domain.VWVersion;
import com.veewap.template.IResultSetHandler;
import com.veewap.template.JdbcTemplate;

public class VWVersionDAOImpl implements IVWVersionDAO {

	@Override
	public int save(VWVersion home) {
		// 
		/**
		 * @author CYT
		 * 	注意 : noticeid 使用了自动增长
			测试了 也可以手动插入
		 */
		String sql = "INSERT INTO VWVersion (id,version,type,path,updateTime,isInuse) VALUES(?,?,?,?,?,?)";
		Object[] params = { home.getId(), home.getVersion(), home.getType(),
				home.getPath(), home.getUpdateTime(), home.getIsInuse()};
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public int update(VWVersion home) {
		String sql = "UPDATE VWVersion SET version=?,type=?,path=?,updateTime=?,isInuse=? WHERE id = ?";
		Object[] params = { home.getVersion(), home.getType(),
				home.getPath(), home.getUpdateTime(), home.getIsInuse(),home.getId()};
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public int delete(Long noticeid) {
		String sql = "DELETE FROM VWVersion WHERE id = ?";
		return JdbcTemplate.update(sql, noticeid);
	}

	@Override
	public VWVersion getLastVersionWith(String type) {
		String sql = "SELECT * FROM VWVersion WHERE type=? ORDER BY version DESC LIMIT 1";
		List<VWVersion> list = JdbcTemplate.query(sql, new VWVersionResultSetHanlder(), type);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public VWVersion get(Long id) {
		String sql = "SELECT * FROM VWVersion WHERE id = ?";
		List<VWVersion> list = JdbcTemplate.query(sql, new VWVersionResultSetHanlder(), id);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}


	@Override
	public List<VWVersion> list() {
		String sql = "SELECT * FROM VWVersion";
		return JdbcTemplate.query(sql, new VWVersionResultSetHanlder());
	}

	
	/**
	 * @author CYT
			
	private Long id;
	private String version;
	private String type;
	private String path;
	private String updateTime;
	private String isInuse;
	 */
	class VWVersionResultSetHanlder implements IResultSetHandler<List<VWVersion>> {
		@Override
		public List<VWVersion> handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<VWVersion> list = new ArrayList<>();
			while (rs.next()) {
				VWVersion home = new VWVersion();
				list.add(home);
				home.setId(rs.getLong("id"));
				home.setVersion(rs.getString("version"));
				home.setType(rs.getString("type"));
				home.setPath(rs.getString("path"));
				home.setUpdateTime(rs.getString("updateTime"));
				home.setIsInuse(rs.getString("isInuse"));
			}
			return list;
		}

	}

}
