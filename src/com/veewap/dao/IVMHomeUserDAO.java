package com.veewap.dao;

import java.util.List;

import com.veewap.domain.VMHomeUser;

public interface IVMHomeUserDAO {

	int save(VMHomeUser home);
	
	int update(VMHomeUser home);
	
	void delete(Long homeId, String name);
	
	// 删除所有
	void delete(Long homeId);
	
	VMHomeUser get(Long id, String name);
	
	List<VMHomeUser> list();

	List<VMHomeUser> getUsernameList(String userName);
	
	// 业务相关
	int exitHome(String homeId, String username, boolean isOnline);
	
	// 需要联表查出Password (比上面多了一个Password)
	VMHomeUser getSyncUserMessage(Long homeid, String inviter);
	
	// 查出所有的信息 可以用于 初始化登录时候的同步
	List<VMHomeUser> getListUserMessage(Long homeid);
}
