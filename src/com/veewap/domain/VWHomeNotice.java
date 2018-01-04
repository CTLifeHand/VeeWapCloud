package com.veewap.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.veewap.dao.impl.VMUserDAOImpl;

public class VWHomeNotice {
	private Long noticeId;
	private String noticeType;
	private Long homeId;
	private String owner;
	private String inviter;
	private String applyer;
	private String agree;
	private String noticeTime;
	
	// 新增其他表(这几个只读就好了)
	private String inviterNickname;
	private String ownerNickname;
	private String applyerNickname;
	@JSONField(name = "VMHomeId")
	private Long VMHomeId;
	
	public Long getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}
	public String getNoticeType() {
		return noticeType;
	}
	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}
	public Long getHomeId() {
		return homeId;
	}
	public void setHomeId(Long homeId) {
		this.homeId = homeId;
	}
	public String getNoticeTime() {
		return noticeTime;
	}
	public void setNoticeTime(String noticeTime) {
		this.noticeTime = noticeTime;
	}
	public Long getVMHomeId() {
		return homeId;
	}
	public void setVMHomeId(Long vMHomeId) {
		VMHomeId = vMHomeId;
	}
	public String getInviterNickname() {
		if (noticeType.equals("DealInvite")) {
			VMUser user = new VMUserDAOImpl().get(inviter);
			if (user == null)  return inviterNickname;
			return user.getNickName();
		}
		return inviterNickname;
	}
	public void setInviterNickname(String inviterNickname) {
		this.inviterNickname = inviterNickname;
	}
	public String getOwnerNickname() {
		if (noticeType.equals("DealApply")||noticeType.equals("InviteHomeUser")) {
			VMUser user = new VMUserDAOImpl().get(owner);
			if (user == null)  return ownerNickname;
			return user.getNickName();
		}
		return ownerNickname;
	}
	public void setOwnerNickname(String ownerNickname) {
		this.ownerNickname = ownerNickname;
	}
	public String getApplyerNickname() {
		if (noticeType.equals("ApplyHomeUser")) {
			VMUser user = new VMUserDAOImpl().get(applyer);
			if (user == null)  return applyerNickname;
			return user.getNickName();
		}
		return applyerNickname;
	}
	public void setApplyerNickname(String applyerNickname) {
		this.applyerNickname = applyerNickname;
	}

	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getInviter() {
		return inviter;
	}
	public void setInviter(String inviter) {
		this.inviter = inviter;
	}
	public String getApplyer() {
		return applyer;
	}
	public void setApplyer(String applyer) {
		this.applyer = applyer;
	}
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	public String getAgree() {
		return agree;
	}
	public void setAgree(String agree) {
		this.agree = agree;
	}
	
	
}
