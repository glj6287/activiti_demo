package com.yoland.activiti.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.yoland.activiti.service.DeploymentProcdefService;
import com.yoland.common.constant.Constants;
import com.yoland.framework.pojo.ResponseEntity;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程部署-->手动
 * 重新部署v+1
 * @author hywin
 *
 */
@Service()
@Transactional
public class DeploymentProcdefServiceImpl implements DeploymentProcdefService {
	
    private final Logger logger = LogManager.getLogger(this.getClass());
	
	 @Autowired
	 private ProcessEngine processEngine;//流程引擎
	 
	 //@Override
	 @SuppressWarnings("rawtypes")
	 public ResponseEntity deployementProcessDefinition(String deploymentName , String deploymentBpmn , String deploymentPng ){
		    logger.info(deploymentName+"流程部署开始");
		    if(StringUtils.isEmpty(deploymentBpmn) || StringUtils.isEmpty(deploymentPng)){
		    	 throw new IllegalArgumentException("部署资源不能为空!");
		    }
		    try {
		    	 Deployment deployment = processEngine.getRepositoryService()//获取流程定义和部署对象相关的Service  
	                        .createDeployment()//创建部署对象  
	                        .name(deploymentName)//声明流程的名称  
	                        .addClasspathResource("activiti/"+deploymentBpmn)//加载资源文件，一次只能加载一个文件  
	                        .addClasspathResource("activiti/"+deploymentPng) 
	                        .deploy();//完成部署  
		    	  logger.info("部署id:"+deployment.getId()+"部署name:"+deployment.getName()+"部署时间:"+deployment.getDeploymentTime());
		    	  if(!StringUtils.isEmpty(deployment.getId())){
		    		    logger.info(deploymentName+"流程部署结束");
			        	return new ResponseEntity();
		    	  } 	
			} catch (Exception e) {
				throw new RuntimeException("流程部署异常");
			}
	        	return new ResponseEntity(Constants.activiti.ERROR_CODE01,Constants.activiti.ERROR_MSG01);
	    } 
	 
	 @Override
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 public ResponseEntity startProcessInstanceByKey(String procdefKey,String userId){
		 logger.info(procdefKey+"启动流程实例开始");
		 if(StringUtils.isEmpty(procdefKey)){
			  throw new IllegalArgumentException("启动流程实例的key不能为空!");
		 }
		 try {
			 processEngine.getIdentityService().setAuthenticatedUserId(userId);//调用官方的开放API；
			 //启动流程实例  
			 ProcessInstance processInstance = processEngine.getRuntimeService()
						  .startProcessInstanceByKey(procdefKey);//使用流程定义的key的最新版本启动流程  
			 logger.info("流程实例ID："+processInstance.getId()+"流程定义的ID："+processInstance.getProcessDefinitionId());
			 if(!StringUtils.isEmpty(processInstance.getId())){
	    		    Map<String,Object> mapRet = new HashMap<String,Object>();
	    		    mapRet.put("processInstanceId", processInstance.getId());
	    		    mapRet.put("businessKey", procdefKey);
	    		    logger.info(procdefKey+"启动流程实例结束");
		        	return new ResponseEntity(mapRet);
	    	  } 
		} catch (Exception e) {
			throw new RuntimeException("启动流程实例结束");
		}
     	return new ResponseEntity(Constants.activiti.ERROR_CODE02,Constants.activiti.ERROR_MSG02);
	 }
	 
	 @Override
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 public ResponseEntity startProcessInstanceByKeyAndVariables(String procdefKey,String userId,Map<String, Object> variables){
		 logger.info(procdefKey+"启动流程实例开始");
		 if(StringUtils.isEmpty(procdefKey)){
			  throw new IllegalArgumentException("启动流程实例的key不能为空!");
		 }
		 if(variables==null || variables.size()==0){
			  throw new IllegalArgumentException("全局变量不能为空!");
		 }
		 //启动流程实例  
		 try {
			 processEngine.getIdentityService().setAuthenticatedUserId(userId);//调用官方的开放API；
			 //variables 可以是变量 如userId 条件   监听器 都可以
			 ProcessInstance processInstance = processEngine.getRuntimeService() 
						 .startProcessInstanceByKey(procdefKey, variables);//使用流程定义的key的最新版本 以及加载variables
			 logger.info("流程实例ID："+processInstance.getId()+"流程定义的ID："+processInstance.getProcessDefinitionId());
			 if(!StringUtils.isEmpty(processInstance.getId())){
	    		    Map<String,Object> mapRet = new HashMap<String,Object>();
	    		    mapRet.put("processInstanceId", processInstance.getId());
	    		    mapRet.put("businessKey", procdefKey);
	    		    logger.info(procdefKey+"启动流程实例结束");
	    		    return new ResponseEntity(mapRet);
	    	  } 
		} catch (Exception e) {
			throw new RuntimeException("启动流程实例异常");
		}
		 return new ResponseEntity(Constants.activiti.ERROR_CODE02,Constants.activiti.ERROR_MSG02);
	 }


}
