package com.veewap.test;

import java.util.List;

import org.junit.Test;

import com.veewap.dao.IVMUserDAO;
import com.veewap.dao.impl.VMUserDAOImpl;
import com.veewap.domain.VMUser;

public class VMUserDAOTest {

	// DAO只初始化一次
	private IVMUserDAO dao = new VMUserDAOImpl();

	@Test
	public void testList() {
		List<VMUser> list = dao.list();
		for (VMUser p : list) {
			System.out.println(p);
		}
	}

	@Test
	public void testGet() {
		VMUser home = dao.get("15217363900");
		System.out.println(home);

		VMUser home2 = dao.get(11L);
		System.out.println(home2);
	}

	// 删
	@Test
	public void testDelete() {
		dao.delete(998L);
	}

	// 增
	@Test
	public void testSave() {
		VMUser home = dao.get(1L);
		System.out.println(home);
		home.setId(998L);
		// home.setHomeMessage("I'm Message 😄");;
		dao.save(home);
		System.out.println(home);
	}

	// 改
	@Test
	public void testUpdate() {
		VMUser home = dao.get(998L);
		System.out.println(home);
		// home.setHomeMessage("I'm Message 😄");

		dao.update(home);
		System.out.println(home);
	}

	// 业务
	@Test
	public void testGetHomeArray() {
		
		
	}

}
