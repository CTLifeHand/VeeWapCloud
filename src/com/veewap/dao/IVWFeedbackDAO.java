package com.veewap.dao;

import java.util.List;

import com.veewap.domain.VWFeedback;

public interface IVWFeedbackDAO {

	void save(VWFeedback home);
	void update(VWFeedback home);
	void delete(Long id);
	
	VWFeedback get(Long id);
	
	List<VWFeedback> list();

	List<VWFeedback> getFeedbackArray(int offset, long length, int timeLimit);
}
