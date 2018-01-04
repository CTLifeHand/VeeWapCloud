package com.veewap.test;

import java.util.List;

import org.junit.Test;

import com.veewap.dao.IVWFeedbackDAO;
import com.veewap.dao.impl.VWFeedbackDAOImpl;
import com.veewap.domain.VWFeedback;

public class VWFeedbackDAOTest {

	// DAO只初始化一次
	private IVWFeedbackDAO dao = new VWFeedbackDAOImpl();

	@Test
	public void testList() {
		List<VWFeedback> list = dao.list();
		for (VWFeedback p : list) {
			System.out.println(p);
		}
	}

	@Test
	public void testGet() {
		VWFeedback home = dao.get(123123L);
		System.out.println(home);
	}

	// 删
	@Test
	public void testDelete() {
		dao.delete(998L);
	}

	// 增
	@Test
	public void testSave() {
		VWFeedback home = dao.get(1L);
		System.out.println(home);
		home.setId(998L);
		// home.setHomeMessage("I'm Message 😄");;
		dao.save(home);
		System.out.println(home);
	}

	// 改
	@Test
	public void testUpdate() {
		VWFeedback home = dao.get(998L);
		System.out.println(home);
		// home.setHomeMessage("I'm Message 😄");

		dao.update(home);
		System.out.println(home);
	}

	// 业务
	@Test
	public void testGetFeedbackArray() {
		List<VWFeedback> list = dao.getFeedbackArray(-1, -1, 300);
		System.out.println(list.size());
		System.out.println(list);
		for (VWFeedback p : list) {
			System.out.println(p);
		}
	}

}
