package com.yoland.activiti.service;

import com.yoland.framework.pojo.ResponseEntity;

public interface HistoryProcessService {

	/**
	 * 个人已办任务分页列表
	 * @param userId
	 * @param pageIndexStr
	 * @param pageSizeStr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity findHisActivitiList(String userId, String pageIndexStr,
									   String pageSizeStr);
	
	/**
	 * 个人查询历史任务列表
	 * @param assignee
	 * @param pageIndexStr
	 * @param pageSizeStr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity findHisTaskList(String assignee, String pageIndexStr,
			String pageSizeStr);
	
	/**
	 * 历史备注列表查询
	 * @param taskId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity getCommentByTaskId(String taskId);




	

}
