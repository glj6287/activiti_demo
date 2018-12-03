package com.yoland.activiti.pojo;

import java.io.Serializable;

/**
 * 高净值系统
 * 返回任务参数对象
 * 
 * @author hywin
 *
 */
public class ResponseTask implements Serializable {

	private static final long serialVersionUID = 7162874268478185555L;
	
	/****-------任务的基本信息------------**/
	private String taskId;//任务ID
	private String taskName;//任务名称
	private String taskCreateTime;//任务创建时间
	private String taskAssignee;//当前任务分配的人
	private String taskGroup;//任务分配给某个组
	private String procInstId;//流程实例Id
	private String procDefId;//流程定义Id  --> 先有流程定义  实例化 多个 流程实例
	private String procDefName;//流程定义  名称  --> 流程名称
	
	/****-------任务的操作信息------------**/
	private String taskClaimTime;//任务领取时间
	private String taskStartTime;//任务开始时间
	private String taskEndTime;//任务结束时间
	private String taskCompleteComment;//任务备注
	private String taskCompleteResult;//任务完成结果-->从变量获取variableLocal
	private String taskResultReason;//任务完成原因
	
	/****-------任务的其他信息------------**/
	private String taskDescribe;//任务描述
	private String procStartUser;//流程发起人
	private String procStartCreateTime;//流程发起时间
	private String procStartEndTime;//流程结束时间
	private String formKey;//外置表单详情页面
	private String businessKey;//启动流程的key
	private String usertaskKey;//当前任务节点的key
	
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskCreateTime() {
		return taskCreateTime;
	}
	public void setTaskCreateTime(String taskCreateTime) {
		this.taskCreateTime = taskCreateTime;
	}
	public String getTaskAssignee() {
		return taskAssignee;
	}
	public void setTaskAssignee(String taskAssignee) {
		this.taskAssignee = taskAssignee;
	}
	public String getTaskGroup() {
		return taskGroup;
	}
	public void setTaskGroup(String taskGroup) {
		this.taskGroup = taskGroup;
	}
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	public String getProcDefId() {
		return procDefId;
	}
	public void setProcDefId(String procDefId) {
		this.procDefId = procDefId;
	}
	public String getProcDefName() {
		return procDefName;
	}
	public void setProcDefName(String procDefName) {
		this.procDefName = procDefName;
	}
	public String getTaskClaimTime() {
		return taskClaimTime;
	}
	public void setTaskClaimTime(String taskClaimTime) {
		this.taskClaimTime = taskClaimTime;
	}
	public String getTaskStartTime() {
		return taskStartTime;
	}
	public void setTaskStartTime(String taskStartTime) {
		this.taskStartTime = taskStartTime;
	}
	public String getTaskEndTime() {
		return taskEndTime;
	}
	public void setTaskEndTime(String taskEndTime) {
		this.taskEndTime = taskEndTime;
	}
	public String getTaskCompleteComment() {
		return taskCompleteComment;
	}
	public void setTaskCompleteComment(String taskCompleteComment) {
		this.taskCompleteComment = taskCompleteComment;
	}
	public String getTaskDescribe() {
		return taskDescribe;
	}
	public void setTaskDescribe(String taskDescribe) {
		this.taskDescribe = taskDescribe;
	}
	public String getProcStartUser() {
		return procStartUser;
	}
	public void setProcStartUser(String procStartUser) {
		this.procStartUser = procStartUser;
	}
	public String getProcStartCreateTime() {
		return procStartCreateTime;
	}
	public void setProcStartCreateTime(String procStartCreateTime) {
		this.procStartCreateTime = procStartCreateTime;
	}
	public String getProcStartEndTime() {
		return procStartEndTime;
	}
	public void setProcStartEndTime(String procStartEndTime) {
		this.procStartEndTime = procStartEndTime;
	}
	public String getFormKey() {
		return formKey;
	}
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}
	public String getTaskCompleteResult() {
		return taskCompleteResult;
	}
	public void setTaskCompleteResult(String taskCompleteResult) {
		this.taskCompleteResult = taskCompleteResult;
	}
	public String getTaskResultReason() {
		return taskResultReason;
	}
	public void setTaskResultReason(String taskResultReason) {
		this.taskResultReason = taskResultReason;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getUsertaskKey() {
		return usertaskKey;
	}
	public void setUsertaskKey(String usertaskKey) {
		this.usertaskKey = usertaskKey;
	}
	
	
	

}
