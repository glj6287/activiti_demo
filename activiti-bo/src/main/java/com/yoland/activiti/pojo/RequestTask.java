package com.yoland.activiti.pojo;

import java.io.Serializable;
import java.util.Map;

import com.yoland.framework.pojo.Page;

public class RequestTask extends Page implements Serializable{

	private static final long serialVersionUID = -2466907715263782107L;
	
	private String userId;//登录用户名 即activit库中user表ID
	private String taskId;//任务ID
	private String procInstId;//流程实例Id
	
	private String taskComment;//完成任务的备注
	private Map<String, Object> variables;//流程变量 -->控制流程节点跳转的条件
	
	private String systemId;//区分系统之间  分别各个系统的用户和组  -->暂时用不到
	
	private String otherAssignee;//任务分配给其他人
	
	private String procdefKey;//启动流程实例的key

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getTaskComment() {
		return taskComment;
	}

	public void setTaskComment(String taskComment) {
		this.taskComment = taskComment;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public String getOtherAssignee() {
		return otherAssignee;
	}

	public void setOtherAssignee(String otherAssignee) {
		this.otherAssignee = otherAssignee;
	}

	public String getProcdefKey() {
		return procdefKey;
	}

	public void setProcdefKey(String procdefKey) {
		this.procdefKey = procdefKey;
	}
	
	
	
	
	
	
	

}
