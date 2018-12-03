package com.yoland.activiti.service;

import com.yoland.framework.pojo.ResponseEntity;

import java.util.Map;

/**
 * 组任务管理
 * @author hywin
 *
 */
public interface TaskGroupProcessService {
    
	/**
	 * 根据组获取待领取任务
	 * @param group
	 * @param pageIndexStr
	 * @param pageSizeStr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity findGroupTaskList(String group, String pageIndexStr,
									 String pageSizeStr);

	/**
	 * 根据userID获取待领取的任务
	 * @param candidateUser
	 * @param pageIndexStr
	 * @param pageSizeStr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity findCandidateUserTaskList(String candidateUser,
			String pageIndexStr, String pageSizeStr);

	/**
	 * 领取任务
	 * @param taskId
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity claimTaskInGroup(String taskId, String userId);

	
	/**
	 * 任务退回组任务
	 * @param taskId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity setTaskAssigneeToNull(String taskId);

	/**
	 * 任务移交给别人
	 * @param taskId
	 * @param assignee
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity setAssignee(String taskId, String assignee);

	/**
	 * 完成任务 备注可空
	 * @param taskId
	 * @param userId
	 * @param comment
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity completeTask(String taskId, String userId, String comment);

	/**
	 * 条件下完成任务 备注可空
	 * @param taskId
	 * @param variables
	 * @param userId
	 * @param comment
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity completeTask(String taskId, Map<String, Object> variables,
			String userId, String comment);

}
