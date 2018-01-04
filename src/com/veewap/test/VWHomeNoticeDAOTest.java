package com.veewap.test;

import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.veewap.dao.IVWHomeNoticeDAO;
import com.veewap.dao.impl.VWHomeNoticeDAOImpl;
import com.veewap.domain.VWHomeNotice;

public class VWHomeNoticeDAOTest {

	// DAO只初始化一次
	private IVWHomeNoticeDAO dao = new VWHomeNoticeDAOImpl();

	@Test
	public void testList() {
		List<VWHomeNotice> list = dao.list();
		for (VWHomeNotice p : list) {
			System.out.println(p);
		}
	}

	@Test
	public void testGet() {

		VWHomeNotice notice = dao.get(229L);
		System.out.println(notice.getHomeId());
		if(notice.getHomeId() == 0){
			notice.setHomeId(999L);
		}
		System.out.println(notice.getHomeId());
		
//		int a = 998;
		System.out.println(Boolean.parseBoolean("true"));
//		System.out.println(Boolean.parseBoolean(a));
//		Boolean("1");
		
		System.out.println(Boolean.parseBoolean("1"));
		// 注意 : 经过测试 只有当为String NULL的时候才会不解析  如果类型为NULL 解析出来的是false(boolean) or 0(int)
//		VWHomeNotice notice = dao.get("InviteHomeUser", "13712798883", Long.parseLong("998"));
		
		// {"agree":false,"homeid":998,"inviter":"13800555555","noticeid":208,"noticetype":"ApplyHomeUser","owner":"15217363900"} 
		// 测试 NULL的完全没有打印出来
		String json = JSON.toJSONString(notice);
		System.out.println(json);
	}

	// 删
	@Test
	public void testDelete() {
		dao.delete(998L);
	}

	// 增
	@Test
	public void testSave() {
		// 测试完全没问题 null的插入也是null
		VWHomeNotice notice = new VWHomeNotice();
		notice.setNoticeType("ApplyHomeUser");
		notice.setInviter("13800555555");
		notice.setOwner("15217363900");
		notice.setHomeId(Long.parseLong("998"));
		dao.save(notice);
		System.out.println(notice);
	}

	// 改
	@Test
	public void testUpdate() {
		VWHomeNotice home = dao.get(998L);
		System.out.println(home);
		// home.setHomeMessage("I'm Message 😄");

		dao.update(home);
		System.out.println(home);
	}

	// 业务
	@Test
	public void testGetNotices() {
		List<VWHomeNotice>list = dao.getNoticesWithUsername("15217363900");
		System.out.println(JSON.toJSONString(list));
	}

}
