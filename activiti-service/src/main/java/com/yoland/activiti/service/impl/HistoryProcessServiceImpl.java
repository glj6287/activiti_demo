package com.yoland.activiti.service.impl;

import com.yoland.activiti.service.HistoryProcessService;
import com.yoland.common.constant.Constants;
import com.yoland.framework.pojo.ResponseEntity;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HistoryProcessServiceImpl implements HistoryProcessService {

        private final Logger logger = LogManager.getLogger(this.getClass());

		 @Autowired
		 private ProcessEngine processEngine;//流程引擎
	  
	    /**查询历史活动  --> 已办任务
	     * 问题：HistoricActivityInstance对应哪个表 
	     * 问题：HistoricActivityInstance和HistoricTaskInstance有什么区别
	     * 
	     * act_hi_actinst  -->某一次流程的执行一共经历了多少个活动
	     * */  
	    @Override
	    @SuppressWarnings({ "rawtypes", "unchecked" })
	    public ResponseEntity findHisActivitiList(String userId, String pageIndexStr, String pageSizeStr){
	    	logger.info("个人历史活动查询开始");
	    	//默认分页参数
	   		Integer pageIndex = Constants.PageDefProgarm.PageIndex;
	   		Integer pageSize = Constants.PageDefProgarm.PageSize;
	   		
	    	if(StringUtils.isEmpty(userId)){
		    	throw new IllegalArgumentException("userId不能为空!");
	    	}
	    	if(!StringUtils.isEmpty(pageIndexStr)){
	   			 pageIndex=Integer.valueOf(pageIndexStr);
	   		}
	   		if(!StringUtils.isEmpty(pageSizeStr)){
	   			 pageSize=Integer.valueOf(pageSizeStr);
	   		}
	    	try {
	    		//已办任务几条
	    		long count = processEngine.getHistoryService()  
	    				.createHistoricActivityInstanceQuery()
	    				.taskAssignee(userId)
	    				.count();
	    		//分页列表
	    		List<HistoricActivityInstance> list = processEngine.getHistoryService()  
	    				.createHistoricActivityInstanceQuery()
	    				.taskAssignee(userId)
	    				.listPage(pageIndex, pageSize);
	    				//.processDefinitionId(processDefinitionId) //流程定义ID  
	    				//.processInstanceId(processInstanceId)  
	    				//.list();  
	    		List<Map<String,Object>> listRet = new ArrayList<Map<String,Object>>();
	    		if(list != null && list.size()>0){  
	    			for(HistoricActivityInstance hai : list){   
    				    Map<String,Object> map = new HashMap<String,Object>();
					    map.put("Id", hai.getId());
					    map.put("activityName", hai.getActivityName());
					    listRet.add(map);
	    			}  
	    		}  
	    		 Map<String,Object> mapRet = new HashMap<String,Object>();
			     mapRet.put("count", count);
			     mapRet.put("list", listRet);
				 logger.info("个人历史活动查询结束");
	    		 return new ResponseEntity(mapRet);
			} catch (Exception e) {
				throw new RuntimeException("查询个人历史活动异常");
			}
	    } 
	    
	    
	    /**
	     * 查询历史任务-->act_hi_taskinst
	     * 
	     * @param processDefinitionId
	     * 
	     */
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
	    public ResponseEntity findHisTaskList(String assignee,String pageIndexStr,String pageSizeStr){  
	    	logger.info("个人历史任务查询开始");
	    	//默认分页参数
	   		Integer pageIndex = Constants.PageDefProgarm.PageIndex;
	   		Integer pageSize = Constants.PageDefProgarm.PageSize;
	    	if(StringUtils.isEmpty(assignee)){
	  		     throw new IllegalArgumentException("当前处理人assignee不能为空!");
	    	}
	    	if(!StringUtils.isEmpty(pageIndexStr)){
	   			 pageIndex=Integer.valueOf(pageIndexStr);
	   		}
	   		if(!StringUtils.isEmpty(pageSizeStr)){
	   			 pageSize=Integer.valueOf(pageSizeStr);
	   		}
	    	try {
	    		long count = processEngine.getHistoryService()  
	    				.createHistoricTaskInstanceQuery()
	    				.taskAssignee(assignee)
	    				.count(); 
	    		List<HistoricTaskInstance> list = processEngine.getHistoryService()  
	    				.createHistoricTaskInstanceQuery()
	    				.taskAssignee(assignee)
	    				.listPage(pageIndex, pageSize);
	    				//taskCandidateGroup(candidateGroup)
	    				//.processDefinitionId(processDefinitionId)  
	    				//.processInstanceId(processInstanceId)  
	    				//.list();  
	    		List<Map<String,Object>> listRet = new ArrayList<Map<String,Object>>();
	    		if(list!=null && list.size()>0){  
	    			for(HistoricTaskInstance hti:list){  
	    			    Map<String,Object> map = new HashMap<String,Object>();
					    map.put("Id", hti.getId());
					    map.put("name", hti.getName());
					    map.put("claimTime", hti.getClaimTime());
					    listRet.add(map);
	    			}  
	    		}  
    		    Map<String,Object> mapRet = new HashMap<String,Object>();
   			    mapRet.put("count", count);
   			    mapRet.put("list", listRet);
	    		logger.info("个人历史任务查询结束");
	    		return new ResponseEntity(mapRet);
			} catch (Exception e) {
				throw new RuntimeException("查询个人历史任务异常");
			}
	    }  
	    
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
	    public ResponseEntity getCommentByTaskId(String taskId){
	    	logger.info("历史备注信息查询开始");
	    	if(StringUtils.isEmpty(taskId)){
	    		 throw new IllegalArgumentException("任务id不能为空!");
	    	}
	    	try {
	    		List<Comment> list = processEngine.getTaskService().getTaskComments(taskId);
	    		//进行遍历
	    		List<Map<String,Object>> listRet = new ArrayList<Map<String,Object>>();
	    		if(list!=null && list.size()>0){
	    			for(Comment com:list){
	    				Map<String,Object> mapRet = new HashMap<String,Object>();
	    				mapRet.put("ID", com.getId());
	    				mapRet.put("fullMessage", com.getFullMessage());
	    				mapRet.put("taskId", com.getTaskId());
	    				mapRet.put("processInstanceId", com.getProcessInstanceId());
	    				mapRet.put("userId", com.getUserId());
	    				mapRet.put("time",com.getTime());
	    				listRet.add(mapRet);
	    			}
	    		}
	    		logger.info("历史备注查询结束");
	    		return new ResponseEntity(listRet);
			} catch (Exception e) {
				throw new RuntimeException("历史备注查询异常");
			}
	    }
	    
}
