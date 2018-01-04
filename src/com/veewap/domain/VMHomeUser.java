package com.veewap.domain;


public class VMHomeUser {
	
	private Long homeId;
	private String userName;
	private String permission;
	
	/**
	 * @author CYT
	0:正常用户（或者没有state参数也是正常用户，兼容以前）  
	1:邀请用户加入家庭，但未被确认状态
	2:同意邀请或申请加入家庭（云端同步用，手机端不处理）
	3:拒绝邀请或申请加入家庭（云端同步用，手机端不处理）
	 */
	private String state;
	private String nickName;
	private String myRoomIds;
	private String level;
	private String relate;
	private String remarks;
	private String isControls;
	private String icon;
	
	// 联表属性(使用try catch解决了)
	private String password;
	
	public Long getHomeId() {
		return homeId;
	}
	public void setHomeId(Long homeId) {
		this.homeId = homeId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMyRoomIds() {
		return myRoomIds;
	}
	public void setMyRoomIds(String myRoomIds) {
		this.myRoomIds = myRoomIds;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getRelate() {
		return relate;
	}
	public void setRelate(String relate) {
		this.relate = relate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getIsControls() {
		return isControls;
	}
	public void setIsControls(String isControls) {
		this.isControls = isControls;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	@Override
	public String toString() {
		return "VMHomeUser [homeId=" + homeId + ", userName=" + userName + ", permission=" + permission + ", state="
				+ state + ", myRoomIds=" + myRoomIds + ", level=" + level + ", relate=" + relate + ", remarks="
				+ remarks + ", isControls=" + isControls + ", icon=" + icon + "]";
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	/**
	 * @author CYT
	开发属性
	 */
	private String id;
	
	private String type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
