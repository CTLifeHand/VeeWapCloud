package com.veewap.test;

import java.util.List;

import org.junit.Test;

//import com.alibaba.fastjson.JSON;
import com.veewap.dao.IVWHomeNoticeDAO;
import com.veewap.dao.impl.VWHomeNoticeDAOImpl;
import com.veewap.domain.VWHomeNotice;
import com.veewap.util.TCUtil;

public class TCUtilTest {

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
	}

	// 删
	@Test
	public void testDelete() {
	}

	// 增
	@Test
	public void testSave() {
	}

	// 改
	@Test
	public void testUpdate() {
		
	}

	// 业务
	@Test
	public void testGetHomeArray() {
		String params = "{\"name\":\"" + "15217363900" + "\"}";
		String message = TCUtil.TaobaoClientSMS("15217363900", "雨蛙智能", params, "SMS_68590049");
		if (message != null && !message.isEmpty()) {
			System.out.println(message);
		}
	}

}
