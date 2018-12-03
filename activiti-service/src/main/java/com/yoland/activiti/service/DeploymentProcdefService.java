package com.yoland.activiti.service;

import com.yoland.framework.pojo.ResponseEntity;

import java.util.Map;

/**
 * 流程部署接口
 * @author hywin
 *
 */
public interface DeploymentProcdefService {
	
//  启动 去重 —> spring 托管 不暴露
//	@SuppressWarnings("rawtypes")
//	ResponseEntity deployementProcessDefinition(String deploymentName,
//			String deploymentBpmn, String deploymentPng);

	/**
	 * 根据key启动流程实例
	 * @param procdefKey
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity startProcessInstanceByKey(String procdefKey, String userId);

	/**
	 * 根据key以及全局变量启动流程实例
	 * @param procdefKey
	 * @param variables
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity startProcessInstanceByKeyAndVariables(String procdefKey,String userId,
			Map<String, Object> variables);

}
