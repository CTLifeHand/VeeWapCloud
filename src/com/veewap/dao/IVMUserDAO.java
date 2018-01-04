package com.veewap.dao;

import java.util.List;

import com.veewap.domain.VMUser;

public interface IVMUserDAO {
	void save(VMUser home);
	void update(VMUser home);
	void delete(Long id);

	VMUser get(Long id);
	VMUser get(String username);
	
	List<VMUser> list();
	
	// 这个方法利用一个参数 power
	boolean checkAdminLogin(String username, String password);
}
