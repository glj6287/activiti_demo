package com.yoland.activiti.pojo;

import com.yoland.framework.pojo.Page;

import java.io.Serializable;


public class ActIdGroup extends Page implements Serializable{
	
	private static final long serialVersionUID = -2651858409490165711L;
	
	private String id ;
	private int rev;
	private String name;
	private String type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getRev() {
		return rev;
	}
	public void setRev(int rev) {
		this.rev = rev;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	

}
