package com.yoland.activiti.pojo;

import java.io.Serializable;
import java.util.List;

public class ActIdUserVo implements Serializable{
	
	private static final long serialVersionUID = 135527568858785339L;
	
	private List<ActIdUser> ActIdUserList;
	private String groupId;
	public List<ActIdUser> getActIdUserList() {
		return ActIdUserList;
	}
	public void setActIdUserList(List<ActIdUser> actIdUserList) {
		ActIdUserList = actIdUserList;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	 
	

}
