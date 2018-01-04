package com.veewap.dao;

import java.util.List;

import com.veewap.domain.VWVersion;



public interface IVWVersionDAO {

	// 普通的增删改查
	int save(VWVersion home);
	int update(VWVersion home);
	int delete(Long id);
	
	VWVersion get(Long id);
	
	List<VWVersion> list();
	
	
	/**
	 * @author CYT
	 *	type=0 表示 android
	 * 	type=1 表示 iOS
	 */
	VWVersion getLastVersionWith(String type);
	
	
}
