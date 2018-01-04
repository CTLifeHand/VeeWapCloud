package com.veewap.test;

import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.veewap.dao.IVMHomeUserDAO;
import com.veewap.dao.impl.VMHomeUserDAOImpl;
import com.veewap.domain.VMHomeUser;

public class VMHomeUserDAOTest {

	// DAO只初始化一次
	private IVMHomeUserDAO dao = new VMHomeUserDAOImpl();

	@Test
	public void testList() {
		List<VMHomeUser> list = dao.list();
		for (VMHomeUser p : list) {
			System.out.println(p);
		}
	}

	@Test
	public void testGet() {
		VMHomeUser home = dao.get(1500877661888L, "15217363900");
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
		VMHomeUser home = dao.get(998L,"152");
		System.out.println(home);
		// home.setHomeMessage("I'm Message 😄");;
		dao.save(home);
		System.out.println(home);
	}

	// 改
	@Test
	public void testUpdate() {
		VMHomeUser home = dao.get(998L,"152");
		// home.setHomeMessage("I'm Message 😄");

		dao.update(home);
		System.out.println(home);
	}

	// 业务
	@Test
	public void testService() {
//		List<VMHomeUser> list = dao.list();
//		System.out.println(list.size());
//		System.out.println(list);
//		for (VMHomeUser p : list) {
//			System.out.println(p);
//		}
		VMHomeUser user = dao.getSyncUserMessage(998L, "13712798883");
		System.out.println(user);
		System.out.println(JSON.toJSONString(user));
	}

}
