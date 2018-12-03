package com.yoland.activiti.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.yoland.activiti.service.TaskGroupProcessService;
import com.yoland.common.constant.Constants;
import com.yoland.framework.pojo.ResponseEntity;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 组任务的概念
 * group->角色
 * 候选人 参与者 监听器
 * @author hywin
 *
 */
@Service
@Transactional
public class TaskGroupProcessServiceImpl implements TaskGroupProcessService {
	
    private final Logger logger = LogManager.getLogger(this.getClass());

	 @Autowired
	 private ProcessEngine processEngine;//流程引擎

	 /**
      * 一般配合监听器使用
      * 或者动态配置${group}
      */
	 @Override
	 @SuppressWarnings({ "unchecked", "rawtypes" })
   	 public ResponseEntity findGroupTaskList(String group, String pageIndexStr , String pageSizeStr){
   		 logger.info("组任务查询开始");
   		 //默认分页参数
   		 Integer pageIndex = Constants.PageDefProgarm.PageIndex;
   		 Integer pageSize = Constants.PageDefProgarm.PageSize;
   		 
   		 if(StringUtils.isEmpty(group)){
			  throw new IllegalArgumentException("组任务的角色group不能为空!");

   		 }
   		 if(!StringUtils.isEmpty(pageIndexStr)){
   			 pageIndex=Integer.valueOf(pageIndexStr);
   		 }
   		 if(!StringUtils.isEmpty(pageSizeStr)){
   			 pageSize=Integer.valueOf(pageSizeStr);
   		 }
   		 try {
   			 //组任务有几条
   			 long count = processEngine.getTaskService()
   					  .createTaskQuery()
   					  .taskCandidateGroup(group)
   					  .count();
   			 //组任务分页列表
   			 List<Task> list = processEngine.getTaskService()
   					  .createTaskQuery()
   					  .orderByTaskCreateTime()//按照任务的创建时间
   					  .asc()//升序
   					  .taskCandidateGroup(group)
   					  .listPage(pageIndex, pageSize);//进行分页
   					  //.list();
   			  List<Map<String, Object>> listRet = new ArrayList<Map<String, Object>>();
   			  if(list!=null && list.size()>0){
   				    listRet = getListTaskToListMap(list);
   			  }
   			  Map<String,Object> mapRet = new HashMap<String,Object>();
   			  mapRet.put("count", count);
   			  mapRet.put("list", listRet);
   			  logger.info("组任务查询结束");
   			  return new ResponseEntity(mapRet);
   		} catch (Exception e) {
   			throw new RuntimeException("组任务查询异常");
   		}
   	 }
     
	 /**
      * 一般配合监听器使用
      * 或者动态配置${candidateUser}---->${group}
      */
	 @Override
	 @SuppressWarnings({ "rawtypes", "unchecked" })
   	 public ResponseEntity findCandidateUserTaskList(String candidateUser,String pageIndexStr , String pageSizeStr){
   		 logger.info("候选人任务查询开始");
   		 //默认分页参数
   		 Integer pageIndex = Constants.PageDefProgarm.PageIndex;
   		 Integer pageSize = Constants.PageDefProgarm.PageSize;
   		 
   		 if(StringUtils.isEmpty(candidateUser)){
   			 throw new IllegalArgumentException("candidateUser候选人不能为空!");
   		 }
   		 if(!StringUtils.isEmpty(pageIndexStr)){
   			 pageIndex=Integer.valueOf(pageIndexStr);
   		 }
   		 if(!StringUtils.isEmpty(pageSizeStr)){
   			 pageSize=Integer.valueOf(pageSizeStr);
   		 }
   		 try {
   			 //组任务有几条
   			 long count = processEngine.getTaskService()
   					  .createTaskQuery()
   					  .taskCandidateUser(candidateUser)// 参与者，组任务查询  
   					  .count();
   			 //组任务分页列表
   			 List<Task> list = processEngine.getTaskService()
   					  .createTaskQuery()
   					  .orderByTaskCreateTime()//按照任务的创建时间
   					  .asc()//升序
   					  .taskCandidateUser(candidateUser)//候选人
   					  .listPage(pageIndex, pageSize);//进行分页
   					  //.list();
   			 List<Map<String, Object>> listRet = new ArrayList<Map<String, Object>>();
   			 if(list!=null && list.size()>0){
   				  listRet = this.getListTaskToListMap(list);
   			  }
   			  //返回页面
   			  Map<String,Object> mapRet = new HashMap<String,Object>();
   			  mapRet.put("count", count);
   			  mapRet.put("list", listRet);
   			  logger.info("候选人任务查询结束");
   			  return new ResponseEntity(mapRet);
   		} catch (Exception e) {
   			throw new RuntimeException("候选人任务查询异常");
   		}
   	 }
     
     /**
      * 任务领取
      * 最好只有对应组内的人员才可以领取
      * 一旦被领取 别人就不能被领取
      * @param taskId
      * @param userId
      * @return
      */
	 @Override
     public ResponseEntity<Object> claimTaskInGroup(String taskId,String userId ){  
    	    logger.info("任务领取开始");
    	    if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(userId)){
    	    	throw new IllegalArgumentException("任务ID或userID不能为空!");
    	    }
	        try {
	        	processEngine.getTaskService() 
	        	       .claim(taskId, userId);//个人任务的办理人  
	        	logger.info("任务领取结束");
	        	return new ResponseEntity<Object>();
			} catch (Exception e) {
				throw new RuntimeException("任务领取异常");
			}
	    } 
     
     /**
      * 任务退回组任务
      * 将个人任务再回退到组任务(前提：之前这个任务是组任务)
      * @param taskId
      */
	 @Override
	 @SuppressWarnings("rawtypes")
     public ResponseEntity setTaskAssigneeToNull(String taskId){  
	        logger.info("任务退回组任务开始");
	        if(StringUtils.isEmpty(taskId)){
    	    	throw new IllegalArgumentException("任务ID不能为空!");
	        }
	        try {
	        	processEngine.getTaskService()//  
	        	.setAssignee(taskId, null);//任务ID   
	        	logger.info("任务退回组任务结束");
	        	return new ResponseEntity();
			} catch (Exception e) {
				throw new RuntimeException("任务退回组任务异常");
			}
	    }  
     
	 /**
	  * 任务的移交  
	  * 同级任务交给别人去做
	  * @param taskId
	  * @param assignee
	  */
	  @Override
	  @SuppressWarnings("rawtypes")
	  public ResponseEntity setAssignee(String taskId,String assignee){  
		    logger.info(taskId+":个人任务移交开始-->"+assignee);
		    if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(assignee)){
		    	throw new IllegalArgumentException("个人任务ID或移交对象的ID不能为空!");
		    }
            try {
            	processEngine.getTaskService()//  
            	       .setAssignee(taskId, assignee); //指定任务的办理人  
            	 logger.info(taskId+":个人任务移交结束-->"+assignee);
	    		return new ResponseEntity();
			} catch (Exception e) {
				throw new RuntimeException("个人任务移交异常");
			}
	    }  
     
     /**
      * 将taskList  --> mapList
      * 直接返回前端 会报lazy loading outside command context 
      * @param list
      * @return
      */
     public List<Map<String,Object>> getListTaskToListMap(List<Task> list){
    	 List<Map<String,Object>> listRet = new ArrayList<Map<String,Object>>();
    	 for (Task task : list) {
				Map<String,Object> map = new HashMap<String, Object>();
				//map.put("1",task.)
				map.put("taskId", task.getId());
				map.put("taskName", task.getName());
				map.put("taskAssignee", task.getAssignee());//任务的处理人
				map.put("taskCreateTime;", task.getCreateTime());
				map.put("taskProcessInstanceId", task.getProcessInstanceId());//流程实例ID
				listRet.add(map);
	      }
    	 return listRet;
     }
	
     
	 @Override
	 @SuppressWarnings("rawtypes")
	 public ResponseEntity completeTask(String taskId ,String userId,String comment){  
		    logger.info(taskId+":个人任务完成开始");
		    if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(userId)){
		    	throw new IllegalArgumentException("个人任务ID或用户Id不能为空!");
		    }
	    	try {
	    		//添加备注
	    		if(!StringUtils.isEmpty(comment)){
	    			// 使用任务id,获取任务对象，获取流程实例id
	    			Task task=processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
	    			//利用任务对象，获取流程实例id
	    			String processInstancesId=task.getProcessInstanceId();
	    			Authentication.setAuthenticatedUserId(userId); // 添加批注时候的审核人，通常应该从session获取
	    			Comment commentRet = processEngine.getTaskService().addComment(taskId,processInstancesId,comment);
	    		    logger.info("备注："+commentRet.getFullMessage());
	    		    if(StringUtils.isEmpty(commentRet.getFullMessage())){
	    		    	return new ResponseEntity(Constants.activiti.ERROR_CODE03,Constants.activiti.ERROR_MSG03);
	    		    }
	    		}
	    		processEngine.getTaskService().complete(taskId);//任务ID  
	    		logger.info(taskId+":个人任务完成结束");
	    		return new ResponseEntity();
			} catch (Exception e) {
				throw new RuntimeException("个人任务完成异常");
			}
	    }  
     
	 @Override
	 @SuppressWarnings("rawtypes")
  	 public ResponseEntity completeTask(String taskId ,Map<String, Object> variables,String userId,String comment){  
  		    logger.info(taskId+":个人任务完成开始");
  		    if(StringUtils.isEmpty(taskId)){
  		    	throw new IllegalArgumentException("个人任务ID或用户Id不能为空!");
  		    }
  		    if(variables==null || variables.size()==0){
  		    	throw new IllegalArgumentException("变量不能为空!");
  		    }
  	    	try {
  	    		//添加备注
  	    		if(!StringUtils.isEmpty(comment)){
  	    			// 使用任务id,获取任务对象，获取流程实例id
  	    			Task task=processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
  	    			//利用任务对象，获取流程实例id
  	    			String processInstancesId=task.getProcessInstanceId();
  	    			Authentication.setAuthenticatedUserId(userId); // 添加批注时候的审核人，通常应该从session获取
  	    			Comment commentRet = processEngine.getTaskService().addComment(taskId,processInstancesId,comment);
  	    			logger.info("备注："+commentRet.getFullMessage());
 	    		    if(StringUtils.isEmpty(commentRet.getFullMessage())){
 	    		    	return new ResponseEntity(Constants.activiti.ERROR_CODE03,Constants.activiti.ERROR_MSG03);
 	    		    }
  	    		}
  	    		processEngine.getTaskService().complete(taskId, variables);
  	    		logger.info(taskId+":个人任务完成结束");
  	    		return new ResponseEntity();
  			} catch (Exception e) {
  				throw new RuntimeException("个人任务完成异常");
  			}
  	    }  
	 
}
