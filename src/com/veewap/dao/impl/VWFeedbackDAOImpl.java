package com.veewap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.veewap.dao.IVWFeedbackDAO;
import com.veewap.domain.VWFeedback;
import com.veewap.template.IResultSetHandler;
import com.veewap.template.JdbcTemplate;


public class VWFeedbackDAOImpl implements IVWFeedbackDAO {

	@Override
	public void save(VWFeedback home) {
		if (home.getId() == 0 ) {
			// 这个表格是自动增长
			String sql = "INSERT INTO vwfeedback (homeId,mobile,feedback,createTime) VALUES(?,?,?,?)";
			Object[] params = { home.getHomeId(), home.getMobile(), home.getFeedback(), home.getCreateTime()};
			JdbcTemplate.update(sql, params);
		}else{
			String sql = "INSERT INTO vwfeedback (id,homeId,mobile,feedback,createTime) VALUES(?,?,?,?,?)";
			Object[] params = { home.getId(), home.getHomeId(), home.getMobile(), home.getFeedback(), home.getCreateTime()};
			JdbcTemplate.update(sql, params);
		}
	}

	@Override
	public void update(VWFeedback home) {
		String sql = "UPDATE vwfeedback SET homeId=?,mobile=?,feedback=?,createTime=? WHERE id = ?";
		Object[] params = { home.getId(), home.getHomeId(), home.getMobile(), home.getFeedback(), home.getCreateTime(), home.getId()};
		JdbcTemplate.update(sql, params);
	}

	@Override
	public void delete(Long id) {
		String sql = "DELETE FROM vwfeedback WHERE id = ?";
		JdbcTemplate.update(sql, id);
	}

	@Override
	public VWFeedback get(Long id) {
		String sql = "SELECT * FROM vwfeedback WHERE id = ?";
		List <VWFeedback> list = JdbcTemplate.query(sql, new VWFeedbackResultSetHanlder(), id);
		if (list.size()==1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<VWFeedback> list() {
		String sql = "SELECT * FROM vwfeedback";
		return JdbcTemplate.query(sql, new VWFeedbackResultSetHanlder());
	}
	
	/**
	 * @author CYT
		private Long id;
		private String homeId;
		private String mobile;
		private String feedback;
		private String createTime;
	 */
	class VWFeedbackResultSetHanlder implements IResultSetHandler<List<VWFeedback>>{
		@Override
		public List<VWFeedback> handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<VWFeedback> list = new ArrayList<>();
			while (rs.next()) {
				VWFeedback home = new VWFeedback();
				list.add(home);
				home.setId(rs.getLong("id"));
				home.setHomeId(rs.getString("homeId"));
				home.setMobile(rs.getString("mobile"));
				home.setFeedback(rs.getString("feedback"));
				home.setCreateTime(rs.getString("createTime"));
			}
			return list;
		}
		
	}

	
	public List<VWFeedback> getFeedbackArray(int offset, long length, int timeLimit){
		String sql;
		String timeLimitStr = "";
		if(timeLimit > 0){
			long time = new Date().getTime();
			time = time - ((long)timeLimit * 24 * 3600 * 1000);
			timeLimitStr = new SimpleDateFormat("yyyy-MM-dd").format(time);
//			timeLimitStr = "where createtime>" + timeLimitStr;
		}
		
		
		/* (non-Javadoc)
		if(offset < 1 && length < 1){ //无限制
			sql = "select * from VWFeedback" + timeLimitStr;
		}
		else{
			offset = offset < 1 ? 0 : offset;
			sql = "select * from VWFeedback" + timeLimitStr
					+ " LIMIT " + offset + "," + length;
		}
		sql = "select * from VWFeedback" + timeLimitStr + " LIMIT " + offset + "," + length;
	 */
		// 是否加Limit
		if(offset < 1 && length < 1){ //无限制
			offset = 0;
			length = java.lang.Long.MAX_VALUE;
		}
		else{
			offset = offset < 1 ? 0 : offset;
		}
		// 注意 : sql = "select * from VWFeedback ? LIMIT ? , ?"; 这样是不行的
		sql = "select * from VWFeedback where createtime>? LIMIT ? , ?";
		
		System.out.println(sql);
		return JdbcTemplate.query(sql, new VWFeedbackResultSetHanlder(),timeLimitStr,offset,length);
	}

}
