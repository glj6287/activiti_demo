package com.yoland.activiti.service;

import com.yoland.activiti.pojo.RequestTask;
import com.yoland.activiti.pojo.ResponseTask;
import com.yoland.framework.pojo.ResponseEntity;

import java.util.List;


/**
 *工作流平台：
 *   1.高净值用户系统 /高金项目
 * 2017-06-08
 * 组任务管理
 * @author hywin
 *
 */
public interface ActivitiTaskManageService {
	
	/**
	 * 启动流程实例
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<ResponseTask> startProcessInstanceByKey(RequestTask requestTask);
	
	/**
	 * 根据登录用户ID-->可能存在多个组
	 * 获取待领取任务的总数
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<Object> getToClaimTaskCounts(RequestTask requestTask);
	
	/**
	 * 根据登录用户ID
	 * 获取待办任务的总数
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<Object>  getToCompleteTaskCounts(RequestTask requestTask);
	
	/**
	 * 根据userid
	 * 获取分页待领取列表
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<List<ResponseTask>> getToClaimTaskList(RequestTask requestTask);
	
	/**
	 * 根据userid
	 * 获取分页待办列表
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<List<ResponseTask>> getToCompleteTaskList(RequestTask requestTask);
	
	/**
	 * 用户id 任务ID
	 * 领取任务  
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<Object> claimTaskByUserId(RequestTask requestTask);
	
	/**
	 * 根据userid
	 * 已办任务
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<List<ResponseTask>> getCompleteTaskToHisTask(RequestTask requestTask);
	
	/**
	 * userid  流程实例ID procInstId
	 * 具体流程已办的任务节点
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<List<ResponseTask>> getHisActListByProcInstId(RequestTask requestTask);
	
	/**
	 * 根据userid taskid
	 * 完成任务
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<ResponseTask> CompleteTaskByUserId(RequestTask requestTask);
	
	/**
	 * 获取当前节点标红的png
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<Object> currentTaskNodePng(RequestTask requestTask);

	/**
	 * 退回任务重新待领取
	 * @param taskId
	 * @return
	 */
	ResponseEntity<Object> returnTaskToAgainClaim(RequestTask requestTask);

	/**
	 * 任务移交给同组下的其他人
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<Object> transferTaskToOtherAssignee(RequestTask requestTask);

	/**
	 * 中止流程
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<Object> endProcess(RequestTask requestTask);

	/**
	 * 根据流程实例中止流程
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<Object> endProcessByProcInstId(RequestTask requestTask);

	/**
	 * 根据流程实例ID获取任务ID
	 * @param requestTask
	 * @return
	 */
	ResponseEntity<ResponseTask> findTaskIdByPId(RequestTask requestTask);

	
	
	
	
          
	

}
