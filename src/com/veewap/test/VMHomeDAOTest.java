package com.veewap.test;

import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.veewap.dao.IVMHomeDAO;
import com.veewap.dao.impl.VMHomeDAOImpl;
import com.veewap.domain.VMHome;

public class VMHomeDAOTest {

	// DAO只初始化一次
	private IVMHomeDAO dao = new VMHomeDAOImpl();

	@Test
	public void testList() {
		List<VMHome> list = dao.getHomeServers("15217363900");
		for (VMHome p : list) {
			System.out.println(p);
		}
	}

	@Test
	public void testGet() {
		VMHome home = dao.get(123123L);
		System.out.println(home);

		System.out.println(JSON.toJSONString(home));
	}

	// 删
	@Test
	public void testDelete() {
		dao.delete(998L);
	}

	// 增
	@Test
	public void testSave() {
		VMHome home = dao.get(1L);
		System.out.println(home);
		home.setId(998L);
		home.setHomeName("testSave");
		home.setHomeAddress("第一国际");
		home.setCity("123");
		// home.setHomeMessage("I'm Message 😄");;
		dao.save(home);
		System.out.println(home);
	}

	// 改
	@Test
	public void testUpdate() {
		VMHome home = dao.get(998L);
		System.out.println(home);
		home.setHomeName("testUpdate998");
		home.setCity("东莞");
		home.setHomeAddress("第一国际A1012");
		// home.setHomeMessage("I'm Message 😄");

		dao.update(home);
		System.out.println(home);
	}

	// 业务
	@Test
	public void testGetHomeArray() {
		List<VMHome> list = dao.getHomeArray(20, 50, -1);
		System.out.println(list.size());
		System.out.println(list);
		for (VMHome p : list) {
			System.out.println(p);
		}
	}
	

	@Test
	public void getHomeServers(){
		List<VMHome> list = dao.getHomeServers("15217363900");
		String mString = JSON.toJSONString(list);
		System.out.println(mString);
	}

}
