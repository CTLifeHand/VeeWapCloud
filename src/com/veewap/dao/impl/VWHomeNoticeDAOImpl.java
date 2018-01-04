package com.veewap.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.veewap.dao.IVWHomeNoticeDAO;
import com.veewap.domain.VWHomeNotice;
import com.veewap.template.IResultSetHandler;
import com.veewap.template.JdbcTemplate;
import com.veewap.util.TCUtil;

public class VWHomeNoticeDAOImpl implements IVWHomeNoticeDAO {

	@Override
	public int save(VWHomeNotice home) {
		//
		/**
		 * @author CYT 注意 : noticeid 使用了自动增长 测试了 也可以手动插入
		 * 
		 */
		String sql = "INSERT INTO VWHomeNotice (noticetype,homeid,owner,inviter,applyer,agree,noticetime) VALUES(?,?,?,?,?,?,?)";
		Object[] params = { home.getNoticeType(), home.getHomeId(), home.getOwner(), home.getInviter(),
				home.getApplyer(), home.getAgree(), home.getNoticeTime() };
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public int update(VWHomeNotice home) {
		String sql = "UPDATE VWHomeNotice SET noticetype=?,homeid=?,owner=?,inviter=?,applyer=?,agree=?,noticetime=? WHERE noticeid = ?";
		Object[] params = { home.getNoticeType(), home.getHomeId(), home.getOwner(), home.getInviter(),
				home.getApplyer(), home.getAgree(), home.getNoticeTime(), home.getNoticeId() };
		return JdbcTemplate.update(sql, params);
	}

	@Override
	public int delete(Long noticeid) {
		String sql = "DELETE FROM VWHomeNotice WHERE noticeid = ?";
		return JdbcTemplate.update(sql, noticeid);
	}

	@Override
	public VWHomeNotice get(String noticeType, String applyer, String owner) {
		String sql = "SELECT * FROM VWHomeNotice WHERE noticetype=? and applyer=? and owner=?";
		List<VWHomeNotice> list = JdbcTemplate.query(sql, new VWHomeNoticeResultSetHanlder(), noticeType, applyer,
				owner);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public VWHomeNotice get(String noticeType, String inviter, long homeId) {
		String sql = "SELECT * FROM VWHomeNotice WHERE noticetype=? and inviter=? and homeid=?";
		List<VWHomeNotice> list = JdbcTemplate.query(sql, new VWHomeNoticeResultSetHanlder(), noticeType, inviter,
				homeId);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public VWHomeNotice get(Long noticeid) {
		String sql = "SELECT * FROM VWHomeNotice WHERE noticeid = ?";
		List<VWHomeNotice> list = JdbcTemplate.query(sql, new VWHomeNoticeResultSetHanlder(), noticeid);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public VWHomeNotice get(String noticeTime) {
		String sql = "SELECT * FROM VWHomeNotice WHERE noticetime = ?";
		List<VWHomeNotice> list = JdbcTemplate.query(sql, new VWHomeNoticeResultSetHanlder(), noticeTime);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<VWHomeNotice> list() {
		String sql = "SELECT * FROM VWHomeNotice";
		return JdbcTemplate.query(sql, new VWHomeNoticeResultSetHanlder());
	}

	/**
	 * @author CYT private Long noticeid; private String noticetype; private
	 *         Long homeid; private String owner; private String inviter;
	 *         private String applyer; private Boolean agree; private String
	 *         noticetime;
	 */
	class VWHomeNoticeResultSetHanlder implements IResultSetHandler<List<VWHomeNotice>> {
		@Override
		public List<VWHomeNotice> handle(ResultSet rs) throws SQLException {
			List<VWHomeNotice> list = new ArrayList<>();
			while (rs.next()) {
				VWHomeNotice home = new VWHomeNotice();
				list.add(home);
				home.setNoticeId(rs.getLong("noticeid"));
				home.setNoticeType(rs.getString("noticetype"));
				home.setHomeId(rs.getLong("homeid"));
				home.setOwner(rs.getString("owner"));
				home.setInviter(rs.getString("inviter"));
				home.setApplyer(rs.getString("applyer"));
				home.setAgree(rs.getString("agree"));
				home.setNoticeTime(rs.getString("noticetime"));
			}
			return list;
		}

	}

	@Override
	public int deleteOverdueNotice(int day) {
		String sql = "delete from vwhomenotice where noticetime<? and noticetype like 'Deal%'";
		return JdbcTemplate.update(sql, TCUtil.getOverdueDateString(day));
	}

	@Override
	public List<VWHomeNotice> getNoticesWithUsername(String username) {
		String sql = "select * from vwhomenotice" 
				+ " where (noticetype='InviteHomeUser' and inviter=?)"
				+ " or (noticetype='DealApply' and applyer=?)"
				+ " or (noticetype in ('ApplyHomeUser','DealInvite') and owner=?)";

		return JdbcTemplate.query(sql, new VWHomeNoticeResultSetHanlder(),username,username,username);
	}

}
