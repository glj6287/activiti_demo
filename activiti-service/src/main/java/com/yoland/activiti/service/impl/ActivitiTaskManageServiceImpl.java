package com.yoland.activiti.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.yoland.activiti.enums.ActivitiUsertaskEnum;
import com.yoland.activiti.pojo.RequestTask;
import com.yoland.activiti.pojo.ResponseTask;
import com.yoland.activiti.service.ActivitiTaskManageService;
import com.yoland.activiti.service.ProcessCoreService;
import com.yoland.common.constant.Constants;
import com.yoland.framework.pojo.ResponseEntity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *工作流平台：
 *   1.高净值用户系统 /高金项目
 *暴露接口实现类
 *
 *2017-06-08
 *
 * 组任务的概念
 * group->角色
 * 候选人 参与者 监听器
 * @author hywin
 *
 */
@Service
@Transactional
public class ActivitiTaskManageServiceImpl implements ActivitiTaskManageService {
	
	 private static Logger logger = LoggerFactory.getLogger(ActivitiTaskManageServiceImpl.class);

	 @Autowired
	 private ProcessEngine processEngine;//流程引擎
	 
	 @Autowired
	 private ProcessCoreService processCoreService;
	 
	 @Autowired
	 ProcessEngineConfiguration processEngineConfiguration;
	 
     @Autowired
     RepositoryService repositoryService;
     
     @Autowired
     HistoryService historyService;
	 
	 @Override
	 public ResponseEntity<ResponseTask> startProcessInstanceByKey(RequestTask requestTask){
		 logger.info(requestTask.getProcdefKey()+"启动流程实例开始");
		 String procdefKey = requestTask.getProcdefKey();
		 String userId = requestTask.getUserId();
		 if(StringUtils.isEmpty(procdefKey)){
			  throw new IllegalArgumentException("启动流程实例的key不能为空!");
		 }
		 if(StringUtils.isEmpty(userId)){
			  throw new IllegalArgumentException("启动流程实例的userId不能为空!");
		 }
		 try {
			 processEngine.getIdentityService().setAuthenticatedUserId(userId);//调用官方的开放API；
			 //启动流程实例  
			 ProcessInstance processInstance = processEngine.getRuntimeService()
						  .startProcessInstanceByKey(procdefKey);//使用流程定义的key的最新版本启动流程  
			 logger.info("流程实例ID："+processInstance.getId()+";流程定义的ID："+processInstance.getProcessDefinitionId());
			 if(!StringUtils.isEmpty(processInstance.getId())){
				    ResponseTask responseTask = new ResponseTask();
				    responseTask.setProcInstId(processInstance.getId());
				    responseTask.setBusinessKey(processInstance.getBusinessKey());
				    ResponseEntity<ResponseTask> responseEntity = new ResponseEntity<ResponseTask>();
				    responseEntity.setData(responseTask);
				    logger.info("流程实例Id存入对象当前："+responseTask.getProcInstId());
	    		    logger.info(procdefKey+"启动流程实例结束");
		        	return responseEntity;
	    	  } 
		} catch (Exception e) {
			throw new RuntimeException("启动流程实例结束");
		}
     	return new ResponseEntity<ResponseTask>(Constants.activiti.ERROR_CODE02,Constants.activiti.ERROR_MSG02);
	 }

	 
	@Override
	public ResponseEntity<Object> getToClaimTaskCounts(RequestTask requestTask) {
	   logger.info("查询待领任务数开始");
	   String candidateUser = requestTask.getUserId();
	   if(StringUtils.isEmpty(candidateUser)){
		   throw new IllegalArgumentException("用户id不能为空!");
	   }
	   try {
		   //候选人--->归属的各个组
		   long count = processEngine.getTaskService().createTaskQuery().taskCandidateUser(candidateUser).count();
		   ResponseEntity<Object> responseEntity = new ResponseEntity<Object>();
		   responseEntity.setData(count);
		   logger.info("查询待领任务数结束");
		   return responseEntity;
	   } catch (Exception e) {
		   throw new RuntimeException("查询待领任务数异常");
	   }
	}

	/**
	 * 待办任务----->前提已经领取过的任务
	 */
	@Override
	public ResponseEntity<Object> getToCompleteTaskCounts(RequestTask requestTask) {
		String assignee = requestTask.getUserId();
		if(StringUtils.isEmpty(assignee)){
			     throw new IllegalArgumentException("用户id不能为空!");
		}
		try {
			//领取人
			long count = processEngine.getTaskService().createTaskQuery().taskAssignee(assignee).count();
			 ResponseEntity<Object> responseEntity = new ResponseEntity<Object>();
			 responseEntity.setData(count);
			 return responseEntity;
		} catch (Exception e) {
			 throw new RuntimeException("查询待办务数异常");
		}   
	}

	@Override
	public ResponseEntity<List<ResponseTask>> getToClaimTaskList(RequestTask requestTask) {
		 logger.info("待领取任务分页查询开始");
   		 //默认分页参数-->当前第几页
		 requestTask = pagePagramToActPage(requestTask);
   		 Integer pageIndex = requestTask.getPage();
   		 Integer pageSize = requestTask.getPageSize();
   		 //候选人
   		 String candidateUser = requestTask.getUserId();
  	     if(StringUtils.isEmpty(candidateUser)){
  		     throw new IllegalArgumentException("用户id不能为空!");
  	     }
   		 try {
		     //组任务有几条
 			 long count = processEngine.getTaskService().createTaskQuery().taskCandidateUser(candidateUser).count();
 			 //组任务列表
 			 List<Task> list = processEngine.getTaskService()
 					 .createTaskQuery()
 					 .orderByTaskCreateTime()//按照任务的创建时间
   					 .desc()//倒序
 					 .taskCandidateUser(candidateUser)
 					 .listPage(pageIndex, pageSize);//进行分页
 			 List<ResponseTask> listRet = new ArrayList<ResponseTask>();
 			 //对list进行数据处理
 			 if(list!=null && list.size()>0){
     			//转换bean 
 				listRet = getListTaskToListBean(list);
 			 }
  		     //返回最终对象
	  		 ResponseEntity<List<ResponseTask>> responseEntity = new ResponseEntity<List<ResponseTask>>();
	  		 responseEntity.setData(listRet);
	  		 responseEntity.setCount((int) count);
	   	     return responseEntity;
   		} catch (Exception e) {
   			throw new RuntimeException("待领任务分页查询异常");
   		}
	}

	@Override
	public ResponseEntity<List<ResponseTask>> getToCompleteTaskList(RequestTask requestTask) {
		 logger.info("待办任务分页查询开始");
		 String assignee = requestTask.getUserId();
   		 //默认分页参数
		 requestTask = pagePagramToActPage(requestTask);
   		 Integer pageIndex = requestTask.getPage();
   		 Integer pageSize = requestTask.getPageSize();
   		 
   		 if(StringUtils.isEmpty(assignee)){
   			 throw new IllegalArgumentException("用户id不能为空!");
   		 }
   		 try {
   			 //组任务有几条
   			 long count = processEngine.getTaskService()
   					  .createTaskQuery()
   					  .taskAssignee(assignee)// 参与者，组任务查询  
   					  .count();
   			 //组任务分页列表
   			 List<Task> list = processEngine.getTaskService()
   					  .createTaskQuery()
   					  .orderByTaskCreateTime()//按照任务的创建时间
   					  .desc()//倒序
   					  .taskAssignee(assignee)//领取人
   					  .listPage(pageIndex, pageSize);//进行分页
   			 List<ResponseTask> listRet = new ArrayList<ResponseTask>();
   			 if(list!=null && list.size()>0){
   				  listRet = this.getListTaskToListBean(list);
   			  }
   			  //返回页面
   			  ResponseEntity<List<ResponseTask>> responseEntity = new ResponseEntity<List<ResponseTask>>();
   			  responseEntity.setCount((int) count);
   			  responseEntity.setData(listRet);
   			  return responseEntity;
   		} catch (Exception e) {
   			throw new RuntimeException("待办任务分页查询异常");
   		}
	}
	
    /**
     * 将taskList  --> List<ResponseTask>
     * 直接返回前端 会报lazy loading outside command context 
     * @param list
     * @return
     */
    public List<ResponseTask> getListTaskToListBean(List<Task> list){
    	 List<ResponseTask> listTemp = new ArrayList<ResponseTask>();
	   	 for (Task task : list) {
	   		ResponseTask  responseTask = new ResponseTask();
	   		//讲task中的属性值 重新 赋值到对应的 responseTask 对象中
	   		responseTask.setTaskId(task.getId());
	   		responseTask.setTaskName(task.getName());
	   		responseTask.setTaskAssignee(null2String(task.getAssignee()));
	   		responseTask.setTaskCreateTime(Date2String(task.getCreateTime()));
	   		responseTask.setProcDefId(task.getProcessDefinitionId());
	   		responseTask.setProcInstId(task.getProcessInstanceId());//实例ID
	   		responseTask.setProcDefName(getProcDefNameByProcDefKey(task.getProcessDefinitionId()));
	   		responseTask.setTaskDescribe(null2String(task.getDescription()));
	   		try {
	   			//流程发起人相关信息
	   			HistoricProcessInstance hisProcInst = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
	   			responseTask.setProcStartUser(null2String(hisProcInst.getStartUserId()));
	   			responseTask.setProcStartCreateTime(Date2String(hisProcInst.getStartTime()));
	   			responseTask.setProcStartEndTime(Date2String(hisProcInst.getEndTime()));
	   		} catch (Exception e) {
	   			responseTask.setProcStartUser("");
	   			responseTask.setProcStartCreateTime("");
	   			responseTask.setProcStartEndTime("");
			}
	   		try {
	   			HistoricTaskInstance histask = processEngine.getHistoryService()  
				     .createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
	   			responseTask.setTaskClaimTime(Date2String(histask.getClaimTime()));
	   			responseTask.setTaskStartTime(Date2String(histask.getStartTime()));
	   			responseTask.setTaskEndTime(Date2String(histask.getEndTime()));
			} catch (Exception e) {
				responseTask.setTaskClaimTime("");
				responseTask.setTaskStartTime("");
				responseTask.setTaskEndTime("");
			}
	   		responseTask.setFormKey(null2String(task.getFormKey()));
	   		listTemp.add(responseTask);
		 }
	   	 return listTemp;
    }
    
	@Override
	public ResponseEntity<Object> claimTaskByUserId(RequestTask requestTask) {
		String userId = requestTask.getUserId();
		String taskId = requestTask.getTaskId();
		if(StringUtils.isEmpty(userId)){
   			 throw new IllegalArgumentException("用户id不能为空!");
   		}
		if(StringUtils.isEmpty(taskId)){
  			 throw new IllegalArgumentException("任务id不能为空!");
  		}
	    logger.info("任务领取开始");
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
	 *   finished() // 查询已经完成的任务 
	 *   unfinished 
	 *   当然这两个都不加，就是把所有任务都查询出来
	 * 
	 *   已办任务
	 *   --->历史任务
	 *
	 */
	@Override
	public ResponseEntity<List<ResponseTask>> getCompleteTaskToHisTask(RequestTask requestTask) {
		logger.info("历史任务分页查询开始");
		String assignee = requestTask.getUserId();
    	//默认分页参数
		requestTask = pagePagramToActPage(requestTask);
   		Integer pageIndex = requestTask.getPage();
   		Integer pageSize = requestTask.getPageSize();
    	if(StringUtils.isEmpty(assignee)){
  		     throw new IllegalArgumentException("用户id不能为空!");
    	}
    	try {
    		long count = processEngine.getHistoryService()  
    				.createHistoricTaskInstanceQuery()
    				.taskAssignee(assignee)
    				.finished()
    				.count(); 
    		List<HistoricTaskInstance> list = processEngine.getHistoryService()  
    				.createHistoricTaskInstanceQuery()
    				.orderByTaskCreateTime()
    				.desc()
    				.taskAssignee(assignee)
    				.finished() // 查询已经完成的任务 
    				.listPage(pageIndex, pageSize);
    		List<ResponseTask> listRet = new ArrayList<ResponseTask>();
    		if(list!=null && list.size()>0){  
    			listRet = getListHisTaskToListBean(list);
    		}  
    		ResponseEntity<List<ResponseTask>> responseEntity = new ResponseEntity<List<ResponseTask>>();
    		responseEntity.setCount((int) count);
    		responseEntity.setData(listRet);
    		logger.info("个人历史任务查询结束");
    		return responseEntity;
		} catch (Exception e) {
			throw new RuntimeException("查询个人历史任务异常");
		}
	}
	
	/**
	 * 对历史任务列表进行数据处理
	 * @param list
	 * @return
	 */
	public List<ResponseTask> getListHisTaskToListBean(List<HistoricTaskInstance> list){
		List<ResponseTask> listRet = new ArrayList<ResponseTask>();
		for (HistoricTaskInstance historicTaskInstance : list) {
			ResponseTask responseTask = new ResponseTask();
			//给返回bean赋值
			responseTask.setTaskId(historicTaskInstance.getId());//历史任务表ID
			responseTask.setTaskName(historicTaskInstance.getName());
			responseTask.setProcDefId(historicTaskInstance.getProcessDefinitionId());
			responseTask.setProcInstId(historicTaskInstance.getProcessInstanceId());//实例ID
			responseTask.setProcDefName(getProcDefNameByProcDefKey(historicTaskInstance.getProcessDefinitionId()));//流程定义名称
			responseTask.setTaskStartTime(Date2String(historicTaskInstance.getStartTime()));
			responseTask.setTaskEndTime(Date2String(historicTaskInstance.getEndTime()));
			responseTask.setTaskClaimTime(Date2String(historicTaskInstance.getClaimTime()));
			responseTask.setTaskDescribe(null2String(historicTaskInstance.getDescription()));
			responseTask.setTaskAssignee(null2String(historicTaskInstance.getAssignee()));
	   		try {
	   			//流程发起人相关信息
	   			HistoricProcessInstance hisProcInst = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
	   			responseTask.setProcStartUser(null2String(hisProcInst.getStartUserId()));
	   			responseTask.setProcStartCreateTime(Date2String(hisProcInst.getStartTime()));
	   			responseTask.setProcStartEndTime(Date2String(hisProcInst.getEndTime()));
	   		} catch (Exception e) {
	   			responseTask.setProcStartUser("");
	   			responseTask.setProcStartCreateTime("");
	   			responseTask.setProcStartEndTime("");
			}
	   		try {
	   		    //备注
	   			List<Comment> taskComments = processEngine.getTaskService().getTaskComments(historicTaskInstance.getId());
	   			String fullMessage = taskComments.get(0).getFullMessage();
	   			responseTask.setTaskCompleteComment(fullMessage);
			} catch (Exception e) {
				responseTask.setTaskCompleteComment("");
			}
	   		responseTask.setFormKey(null2String(historicTaskInstance.getFormKey()));
	   		//任务完成结果
	   		try {
		   		 HistoricVariableInstance hisVariable = processEngine.getHistoryService()  
			            .createHistoricVariableInstanceQuery()
			            .processInstanceId(historicTaskInstance.getProcessInstanceId())
			            .taskId(historicTaskInstance.getId())
			            .variableName(Constants.TaskStatus.task_status_key)
			            .singleResult();
		   		 responseTask.setTaskCompleteResult((String) hisVariable.getValue());
	   		} catch (Exception e) {
	   			responseTask.setTaskCompleteResult("");
			}
	   		//formkey
	   		responseTask.setFormKey(null2String(historicTaskInstance.getFormKey()));
	   		//任务完成原因
	   		responseTask.setTaskResultReason(null2String(historicTaskInstance.getDeleteReason()));
			listRet.add(responseTask);
		}
		return listRet;
	}
	
	 /**
     * 已办任务
     * 点击一个流程进去
     * 查看该未结束的流程   当前节点之前的操作列表
     * 
     * ----->直接根据流程实例Id查询已办任务详情
     * 
     */
	@Override
	public ResponseEntity<List<ResponseTask>> getHisActListByProcInstId(RequestTask requestTask) {
		logger.info("已办任务流程详情查询开始");
		String procInstId = requestTask.getProcInstId();
    	if(StringUtils.isEmpty(procInstId)){
	    	throw new IllegalArgumentException("流程实例Id不能为空!");
    	}
    	try {
    		//已办任务几条
    		long count = processEngine.getHistoryService()  
    				.createHistoricTaskInstanceQuery()
    				.processInstanceId(procInstId)
    				.count();
    		//分页列表
    		 List<HistoricTaskInstance> list = processEngine.getHistoryService()  
    				.createHistoricTaskInstanceQuery()
    				.orderByTaskCreateTime()
    				.desc()
    				.processInstanceId(procInstId)
    				.list();
    		List<ResponseTask> listRet = new ArrayList<ResponseTask>();
    		if(list!=null && list.size()>0){
    			listRet=getListHisTaskToListBean(list);//详情
    		}
    		ResponseEntity<List<ResponseTask>> responseEntity = new ResponseEntity<List<ResponseTask>>();
    		responseEntity.setCount((int) count);
    		responseEntity.setData(listRet);
			logger.info("已办任务流程详情查询结束");
    		return responseEntity;
		} catch (Exception e) {
			throw new RuntimeException("已办任务流程详情查询异常");
		}
	}

	@Override
	public ResponseEntity<ResponseTask> CompleteTaskByUserId(RequestTask requestTask) {
		    String userId = requestTask.getUserId();
		    String taskId = requestTask.getTaskId();
		    String comment = requestTask.getTaskComment();
		    Map<String, Object> variables = requestTask.getVariables();
		    logger.info(taskId+":个人任务完成开始");
		    if(StringUtils.isEmpty(taskId)){
		    	throw new IllegalArgumentException("个人任务ID或用户Id不能为空!");
		    }
		    Task task=processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
		    //利用任务对象，获取流程实例id
		    String processInstancesId=task.getProcessInstanceId();
	    	try {
	    		//添加备注
	    		if(!StringUtils.isEmpty(comment)){
	    			// 使用任务id,获取任务对象，获取流程实例id
	    			Authentication.setAuthenticatedUserId(userId); // 添加批注时候的审核人，通常应该从session获取
	    			Comment commentRet = processEngine.getTaskService().addComment(taskId,processInstancesId,comment);
	    			logger.info("备注："+commentRet.getFullMessage());
	    		    if(StringUtils.isEmpty(commentRet.getFullMessage())){
	    		    	return new ResponseEntity<ResponseTask>(Constants.activiti.ERROR_CODE03,Constants.activiti.ERROR_MSG03);
	    		    }
	    		}
	    		//给任务节点确定分配人-->即当前完成人
	    		processEngine.getTaskService().setAssignee(taskId, userId);
	    		if(variables==null || variables.size()==0){
	    			processEngine.getTaskService().complete(taskId);
	    		}else{
	    			//局部变量
	    			processEngine.getTaskService().setVariableLocal(taskId, Constants.TaskStatus.task_status_key, variables.get(Constants.TaskStatus.task_status_key));
	    		    //processEngine.getTaskService().setVariable(taskId, Constants.TaskStatus.task_status_key, variables.get(Constants.TaskStatus.task_status_key));
	    			processEngine.getTaskService().complete(taskId, variables);
	    		}
	    		logger.info(taskId+":个人任务完成结束");
	    		ResponseEntity<ResponseTask> responseEntity = new ResponseEntity<ResponseTask>();
	    		//当前任务节点KEY
	    		String curActiviti = getCurActiviti(processInstancesId);
	    		if(StringUtils.isEmpty(curActiviti)){
	    			Date endDate = processCoreService.checkProcessEnd(processInstancesId);
	    			if(endDate!=null){
	    				logger.info("当前流程是否结束，结束时间endDate："+endDate);
	    				curActiviti=ActivitiUsertaskEnum.ENDUSERTASK.getName();
	    			}
	    		}
	    		logger.info("userTaskKey当前："+curActiviti);
	    		ResponseTask responseTask = new ResponseTask();
	    		responseTask.setUsertaskKey(curActiviti);
	    		responseEntity.setData(responseTask);
	    		logger.info("userTaskKey在对象当中："+responseTask.getUsertaskKey());
	    		return responseEntity;
			} catch (Exception e) {
				throw new RuntimeException("个人任务完成异常");
			}
	    }  
	
	/**
	 * 获取当前节点的 usertaskKey
	 * @param procInstanceId
	 * @return
	 */
	 public String getCurActiviti(String procInstanceId){
	    	String curId="";
	    	try {
	    		//38001
	        	//根据流程ID获取当前任务
	        	List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(procInstanceId).list();
	        	for (Task task : tasks) {
	        		//当前流程的流程定义
	        		RepositoryService rs = processEngine.getRepositoryService();
	        		ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl)rs).getDeployedProcessDefinition(task.getProcessDefinitionId());
	        		
	        		List<ActivityImpl> activitiList = def.getActivities(); //rs是指RepositoryService的实例
	        		//执行实例以及当前流程节点的ID
	        		String excId = task.getExecutionId();
	        		ExecutionEntity execution = (ExecutionEntity) processEngine.getRuntimeService().createExecutionQuery().executionId(excId).singleResult();
	        		String activitiId = execution.getActivityId();
	        		//当前节点实例
	        		for(ActivityImpl activityImpl:activitiList){
	        			String id = activityImpl.getId();
	        			if(activitiId.equals(id)){
	        				logger.info("当前任务："+activityImpl.getProperty("name")); //输出某个节点的某种属性
	        				logger.info("当前Id:"+activityImpl.getId());
	        				curId = activityImpl.getId();
	        				List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();//获取从某个节点出来的所有线路
	        				//下一个节点实例
	        				for(PvmTransition tr:outTransitions){
	        					PvmActivity ac = tr.getDestination(); //获取线路的终点节点
	        					logger.info("下一步任务任务："+ac.getProperty("name"));
	        				}
	        				break;
	        			}
	        		}
	    		}
	        	return curId;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return curId;
	}

	 /**
	  * 此方法暂时不用了，用下面的，升级了。
	  * @param requestTask
	  * @return
	  */
	//@Override
	public ResponseEntity<Object> UncurrentTaskNodePng(RequestTask requestTask) {
		 String processInstanceId = requestTask.getProcInstId();
		 if(StringUtils.isEmpty(processInstanceId)){
		    	throw new IllegalArgumentException("流程实例Id不能为空不能为空!");
		  }
		 try {
			 //1.创建核心引擎流程对象processEngine  
		      ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();  
		      TaskService taskService = processEngine.getTaskService();  
		      Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();  
		      //流程定义  
		      BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId());   
		        
		      //正在活动节点  
		      List<String> activeActivityIds = processEngine.getRuntimeService().getActiveActivityIds(task.getExecutionId());  

		      ProcessDiagramGenerator pdg = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();  
		      //生成流图片  
		      InputStream inputStream = pdg.generateDiagram(bpmnModel, "PNG", activeActivityIds, activeActivityIds,    
		              processEngine.getProcessEngineConfiguration().getActivityFontName(),    
		              processEngine.getProcessEngineConfiguration().getLabelFontName(),    
		              //processEngine.getProcessEngineConfiguration().getActivityFontName(),  
		              processEngine.getProcessEngineConfiguration().getProcessEngineConfiguration().getClassLoader(), 1.0); 
		      byte[] input2byte = input2byte(inputStream);
		      
		      ResponseEntity<Object> responseEntity = new ResponseEntity<Object>();
		      responseEntity.setData(input2byte);
		      return responseEntity;
		 } catch (Exception e) {
			 throw new RuntimeException("查看流程图预览失败！");
		 }
	}
	
	/**
	 * 查看流程图升级v2.0接口
	 */
	@Override
	public ResponseEntity<Object> currentTaskNodePng(RequestTask requestTask) {
			String processInstanceId = requestTask.getProcInstId();
			if(StringUtils.isEmpty(processInstanceId)){
			    throw new IllegalArgumentException("流程实例Id不能为空不能为空!");
			}
			try {
	        //获取历史流程实例
	        HistoricProcessInstance processInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
	        //获取流程图
	        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(processInstance.getProcessDefinitionId());
	        processEngineConfiguration = processEngine.getProcessEngineConfiguration();
	        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

	        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
	        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

	        List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
	        //高亮环节id集合
	        List<String> highLightedActivitis = new ArrayList<String>();
	        //高亮线路id集合
	        List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);

	        for(HistoricActivityInstance tempActivity : highLightedActivitList){
	            String activityId = tempActivity.getActivityId();
	            highLightedActivitis.add(activityId);
	        }

	        //中文显示的是口口口，设置字体就好了
	        InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体",null,1.0);
	        //单独返回流程图，不高亮显示
//	        InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
	        byte[] input2byte = input2byte(inputStream);
		      
	        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>();
	        responseEntity.setData(input2byte);
	        return responseEntity;
			} catch (Exception e) {
				 throw new RuntimeException("查看流程图预览失败！");
			}
	    }

	    /**
	     * 获取需要高亮的线
	     * @param processDefinitionEntity
	     * @param historicActivityInstances
	     * @return
	     */
	    private List<String> getHighLightedFlows(
	            ProcessDefinitionEntity processDefinitionEntity,
	            List<HistoricActivityInstance> historicActivityInstances) {
	        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
	        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
	            ActivityImpl activityImpl = processDefinitionEntity
	                    .findActivity(historicActivityInstances.get(i)
	                            .getActivityId());// 得到节点定义的详细信息
	            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
	            ActivityImpl sameActivityImpl1 = processDefinitionEntity
	                    .findActivity(historicActivityInstances.get(i + 1)
	                            .getActivityId());
	            // 将后面第一个节点放在时间相同节点的集合里
	            sameStartTimeNodes.add(sameActivityImpl1);
	            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
	                HistoricActivityInstance activityImpl1 = historicActivityInstances
	                        .get(j);// 后续第一个节点
	                HistoricActivityInstance activityImpl2 = historicActivityInstances
	                        .get(j + 1);// 后续第二个节点
	                if (activityImpl1.getStartTime().equals(
	                        activityImpl2.getStartTime())) {
	                    // 如果第一个节点和第二个节点开始时间相同保存
	                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
	                            .findActivity(activityImpl2.getActivityId());
	                    sameStartTimeNodes.add(sameActivityImpl2);
	                } else {
	                    // 有不相同跳出循环
	                    break;
	                }
	            }
	            List<PvmTransition> pvmTransitions = activityImpl
	                    .getOutgoingTransitions();// 取出节点的所有出去的线
	            for (PvmTransition pvmTransition : pvmTransitions) {
	                // 对所有的线进行遍历
	                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
	                        .getDestination();
	                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
	                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
	                    highFlows.add(pvmTransition.getId());
	                }
	            }
	        }
	        return highFlows;
	    }
	
	
    /**
     * 任务退回组任务
     * 将个人任务再回退到组任务(前提：之前这个任务是组任务)
     * @param taskId
     */
	 @Override
     public ResponseEntity<Object>  returnTaskToAgainClaim(RequestTask requestTask){  
		    String taskId = requestTask.getTaskId();
	        logger.info("任务退回组任务开始");
	        if(StringUtils.isEmpty(taskId)){
	    	throw new IllegalArgumentException("任务ID不能为空!");
	        }
	        try {
	        	processEngine.getTaskService()
	        	  .setAssignee(taskId, null);//任务ID   
	        	logger.info("任务退回组任务结束");
	        	return new ResponseEntity<Object>();
			} catch (Exception e) {
				throw new RuntimeException("任务退回组任务异常");
			}
	    }  
    
	 /**
	  * 任务的移交  
	  * 同级任务交给别人去做
	  * @param requestTask
	  */
	  @Override
	  public ResponseEntity<Object> transferTaskToOtherAssignee(RequestTask requestTask){
		    String taskId = requestTask.getTaskId();
		    String otherAssignee = requestTask.getOtherAssignee();
		    logger.info(taskId+":个人任务移交开始-->"+otherAssignee);
		    if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(otherAssignee)){
		    	throw new IllegalArgumentException("个人任务ID或移交对象的ID不能为空!");
		    }
           try {
        	   //先判断此任务节点是哪个组
   		       IdentityLink identityLink = processEngine.getTaskService().getIdentityLinksForTask(taskId).get(0);
   		       String groupId = identityLink.getGroupId();
   		       List<Group> list = processEngine.getIdentityService().createGroupQuery().groupMember(otherAssignee).list();
   		       List<String> listRet = new ArrayList<String>();
   		       for (Group group : list) {
   		    	    listRet.add(group.getId());
			   }
   		       //此移交用户不是这个组的
   		       if(!listRet.contains(groupId)){
   		    	  return new ResponseEntity<Object>(Constants.activiti.ERROR_CODE04,Constants.activiti.ERROR_MSG04);
   		       }
           	   processEngine.getTaskService()  
           	       .setAssignee(taskId, otherAssignee); //指定任务的办理人  
           	   logger.info(taskId+":个人任务移交结束-->"+otherAssignee);
	    	   return new ResponseEntity<Object>();
			} catch (Exception e) {
				throw new RuntimeException("个人任务移交异常");
			}
	    }  
	
	 
	  @Override
	  public ResponseEntity<Object> endProcess(RequestTask requestTask){  
		    String taskId = requestTask.getTaskId();
	        logger.info("中止流程开始");
	        if(StringUtils.isEmpty(taskId)){
	    	throw new IllegalArgumentException("任务ID不能为空!");
	        }
	        try {
	        	processCoreService.endProcess(taskId);
	        	logger.info("中止流程结束");
	        	return new ResponseEntity<Object>();
			} catch (Exception e) {
				throw new RuntimeException("中止流程异常");
			}
	  } 
	  
		 
	  @Override
	  public ResponseEntity<Object> endProcessByProcInstId(RequestTask requestTask){  
		    String pid = requestTask.getProcInstId();
	        logger.info("中止流程开始");
	        if(StringUtils.isEmpty(pid)){
	    	throw new IllegalArgumentException("流程实例ID不能为空!");
	        }
	        try {
	        	processCoreService.endProcessByPid(pid);
	        	logger.info("中止流程结束");
	        	return new ResponseEntity<Object>();
			} catch (Exception e) {
				throw new RuntimeException("中止流程异常");
			}
	  } 
	  
	  /**
	   * 根据流程实例ID获取任务ID
	   * @param requestTask
	   * @return
	   */
	  @Override
	  public ResponseEntity<ResponseTask> findTaskIdByPId(RequestTask requestTask){
		  ResponseEntity<ResponseTask> resp = new ResponseEntity<ResponseTask>();
		  String processInstanceId = requestTask.getProcInstId();
	      logger.info("根据流程实例ID查询任务ID开始");
	      if(StringUtils.isEmpty(processInstanceId)){
	    	throw new IllegalArgumentException("流程实例ID不能为空!");
	      }
		  try {
			  Task singleResult = processEngine.getTaskService()
					  .createTaskQuery()
					  .processInstanceId(processInstanceId)
					  .singleResult();
			  ResponseTask task = new ResponseTask();
			  task.setTaskId(singleResult.getId());
			  resp.setData(task);
			  logger.info("根据流程实例ID查询任务ID结束");
			  return resp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("根据流程实例ID查询任务ID异常");
		}
	  }
	  
	  
	  //----------------------------工具类-------------------------//
	  
	  public static final byte[] input2byte(InputStream inStream)  
            throws IOException {  
	        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
	        byte[] buff = new byte[100];  
	        int rc = 0;  
	        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
	            swapStream.write(buff, 0, rc);  
	        }  
	        byte[] in2b = swapStream.toByteArray();  
	        return in2b;  
    }  
	
	public static final String Date2String(Date dateTime){
		try {
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime);
			return endTime;
		} catch (Exception e) {
			return "";
		}
	}
	
	public static final String null2String(String str){
		if(StringUtils.isEmpty(str)){
   			return "";
   		}else{
   			return str;
   		}
	}
	
	/**
	 * 根据流程定义的key 获取 流程实定义名称
	 * @param procInstId
	 * @return
	 */
	public String getProcDefNameByProcDefKey(String ProcDefInitId){
		try {
			//流程定义名称
   			ProcessDefinition processDefinition = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
   					.createProcessDefinitionQuery()
   					.processDefinitionId(ProcDefInitId)//使用流程定义ID查询
   					.singleResult();
   			return processDefinition.getName();
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 将前端分页传过来变为acttiviti分页需要
	 * @param requestTask
	 * @return
	 */
	public RequestTask pagePagramToActPage(RequestTask requestTask){
		 //默认分页参数 0-10   10-10  30-10  40-10
		 Integer pageIndex = requestTask.getPage()-1;//当前第几页
  		 Integer pageSize = requestTask.getPageSize();
  		 Integer pageIndexRet = pageIndex*pageSize;
  		 //如果默认第一页,不是第一页
//  		 if(pageIndex!=0){
//  			pageSize=pageSize+requestTask.getPageSize()*pageIndex;
//  		 }
  		 //重新赋值
  		requestTask.setPage(pageIndexRet);
  		//requestTask.setPageSize(pageSize);
  		return requestTask;
	}

	 
}
