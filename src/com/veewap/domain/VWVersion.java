package com.veewap.domain;

public class VWVersion {

	private Long id;
	private String version;
	private String type;
	private String path;
	private String updateTime;
	private String isInuse;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getIsInuse() {
		return isInuse;
	}
	public void setIsInuse(String isInuse) {
		this.isInuse = isInuse;
	}
	
	
	
}
