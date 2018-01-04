package com.veewap.dao;

import java.util.List;

import com.veewap.domain.VMHome;

public interface IVMHomeDAO {

	// 普通的增删改查
	int save(VMHome home);

	int update(VMHome home);

	int delete(Long id);

	VMHome get(Long id);

	List<VMHome> list();

	List<VMHome> getHomeArray(int offset, int length, int isOnline);

	// 业务相关
	int deleteVMHome(Long id);

	void offlineHome(long homeId);

	void setVisual(long vMHomeId);

	void saveHomeMessage(long vMHomeId, String homeMessage);

	List<VMHome> getHomeServers(String username);
	
	
	
}
