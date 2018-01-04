package com.veewap.domain;

import com.alibaba.fastjson.annotation.JSONField;

public class VWFeedback {
	

	private Long id;
	@JSONField(name="VMHomeId")
	private String homeId;
	private String mobile;
	private String feedback;
	
	
	private String createtime;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHomeId() {
		return homeId;
	}
	public void setHomeId(String homeId) {
		this.homeId = homeId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public String getCreateTime() {
		return createtime;
	}
	public void setCreateTime(String createTime) {
		this.createtime = createTime;
	}
	@Override
	public String toString() {
		return "VWFeedback [id=" + id + ", homeId=" + homeId + ", mobile=" + mobile + ", feedback=" + feedback
				+ ", createTime=" + createtime + "]";
	}
	
	
}
