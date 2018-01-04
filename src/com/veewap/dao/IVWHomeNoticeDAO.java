package com.veewap.dao;

import java.util.List;

import com.veewap.domain.VWHomeNotice;


public interface IVWHomeNoticeDAO {

	// 普通的增删改查
	int save(VWHomeNotice home);
	int update(VWHomeNotice home);
	int delete(Long id);
	
	VWHomeNotice get(Long id);
	
	List<VWHomeNotice> list();
	
	
	VWHomeNotice get(String noticeType, String applyer, String owner);
	VWHomeNotice get(String noticeType, String inviter, long homeId);
	VWHomeNotice get(String noticeTime);
	int deleteOverdueNotice(int day);
	List<VWHomeNotice> getNoticesWithUsername(String username);
	
	
}
