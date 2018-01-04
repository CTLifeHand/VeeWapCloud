package com.veewap.domain;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class VMHome {
	private Long id;
	private String homeName;
	private String city;
	private String homeAddress;
	private String homeMessage;
	private String isOnline;
	private int isVisual;
	private int isDelete;

	/**
	 * 开发属性(网页版适配...)
	 */
	@JSONField(name = "OfflineTime")
	private String offlineTime;
	@JSONField(name = "ServerPanelId")
	private int serverPanelId;
	/**
	 * 开发属性(很多是别名)
	 */
	@JSONField(name = "VMHomeId")
	private Long VMHomeId;
	@JSONField(name = "VMHomeName")
	private String VMHomeName;
	@JSONField(name = "VMHomeCity")
	private String VMHomeCity;
	@JSONField(name = "VMCity")
	private String VMCity;
	@JSONField(name = "VMHomeAddress")
	private String VMHomeAddress;
	private String isConnected;
	@JSONField(name = "VMHomeMessage")
	private JSONObject VMHomeMessage;

	public JSONObject getVMHomeMessage() {
		return VMHomeMessage;
	}

	public void setVMHomeMessage(JSONObject vMHomeMessage) {
		VMHomeMessage = vMHomeMessage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHomeName() {
		return homeName;
	}

	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getHomeMessage() {
		return homeMessage;
	}

	public void setHomeMessage(String homeMessage) {
		this.homeMessage = homeMessage;
	}

	public String getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	public String getOfflineTime() {
		return offlineTime;
	}

	public void setOfflineTime(String offlineTime) {
		this.offlineTime = offlineTime;
	}

	public int getIsVisual() {
		return isVisual;
	}

	public void setIsVisual(int isVisual) {
		this.isVisual = isVisual;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getServerPanelId() {
		return serverPanelId;
	}

	public void setServerPanelId(int serverPanelId) {
		this.serverPanelId = serverPanelId;
	}

	@Override
	public String toString() {
		return "VMHome [id=" + id + ", homeName=" + homeName + ", city=" + city + ", homeAddress=" + homeAddress
				+ ", homeMessage=" + homeMessage + ", isOnline=" + isOnline + ", isVisual=" + isVisual + ", isDelete="
				+ isDelete + ", offlineTime=" + offlineTime + ", serverPanelId=" + serverPanelId + ", VMHomeId="
				+ VMHomeId + ", VMHomeName=" + VMHomeName + ", VMHomeCity=" + VMHomeCity + ", VMCity=" + VMCity
				+ ", VMHomeAddress=" + VMHomeAddress + ", isConnected=" + isConnected + ", VMHomeMessage="
				+ VMHomeMessage + "]";
	}

	/**
	 * 开发属性(很多是别名)
	 */
	public Long getVMHomeId() {
		if (VMHomeId != null && VMHomeId > 0) {
			return VMHomeId;
		}
		return id;
	}

	public void setVMHomeId(Long vMHomeId) {
		VMHomeId = vMHomeId;
	}

	public String getVMHomeName() {
		if (VMHomeName != null) {
			return VMHomeName;
		}
		return homeName;
	}

	public void setVMHomeName(String vMHomeName) {
		VMHomeName = vMHomeName;
	}

	public String getVMHomeCity() {
		if (VMHomeCity != null) {
			return VMHomeCity;
		}
		return city;
	}

	public void setVMHomeCity(String vMHomeCity) {
		VMHomeCity = vMHomeCity;
	}

	public String getVMHomeAddress() {
		if (VMHomeAddress != null) {
			return VMHomeAddress;
		}
		return homeAddress;
	}

	public void setVMHomeAddress(String vMHomeAddress) {
		VMHomeAddress = vMHomeAddress;
	}

	public String getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(String isConnected) {
		this.isConnected = isConnected;
	}

	public String getVMCity() {
		if (VMCity != null) {
			return VMCity;
		}
		return city;
	}

	public void setVMCity(String vMCity) {
		VMCity = vMCity;
	}
}
