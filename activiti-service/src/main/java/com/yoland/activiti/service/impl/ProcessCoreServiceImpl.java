package com.yoland.activiti.service.impl;

import com.yoland.activiti.service.ProcessCoreService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 流程中止等相关操作
 * 
 * 此核心类主要处理：流程通过、驳回、会签、转办、中止、挂起等核心操作<br>
 * @author hywin
 *
 */
@Service
@Transactional
public class ProcessCoreServiceImpl implements ProcessCoreService {
	
	 @Autowired
	 private TaskService taskService;
	
	 @Autowired
	 private RepositoryService repositoryService;
	 
	 @Autowired
     private ProcessEngine processEngine;//流程引擎

	 /**
	  * 中止流程(特权人直接审批通过等) 
	  * @param taskId
	  * @throws Exception
	  */
	 @Override
	 public void endProcess(String taskId){  
	        ActivityImpl endActivity = findActivitiImpl(taskId, "end");  
	        commitProcess(taskId, null, endActivity.getId());  
	 }  
	 
	 @Override
	 public void endProcessByPid(String processInstanceId){
		String findTaskIdByPId = findTaskIdByPId(processInstanceId);
		endProcess(findTaskIdByPId);
	 }
	 /**
	  * 根据任务ID和节点ID获取活动节点 <br>
	  * @param taskId
	  * @param activityId
	  *            活动节点ID <br> 
      *            如果为null或""，则默认查询当前活动节点 <br> 
      *            如果为"end"，则查询结束节点 <br> 
	  * @return
	  * @throws Exception
	  */
	 private ActivityImpl findActivitiImpl(String taskId, String activityId){  
	        // 取得流程定义  
	        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);  
	        // 获取当前活动节点ID  
	        if (StringUtils.isEmpty(activityId)) {  
	            activityId = findTaskById(taskId).getTaskDefinitionKey();  
	        }  
	        // 根据流程定义，获取该流程实例的结束节点  
	        if (activityId.toUpperCase().equals("END")) {  
	            for (ActivityImpl activityImpl : processDefinition.getActivities()) {  
	                List<PvmTransition> pvmTransitionList = activityImpl  
	                        .getOutgoingTransitions();  
	                if (pvmTransitionList.isEmpty()) {  
	                    return activityImpl;  
	                }  
	            }  
	        }  
	        // 根据节点ID，获取对应的活动节点  
	        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)  
	                .findActivity(activityId);  
	        return activityImpl;  
	  }  
	  
	 /**
	  * 根据任务ID获取流程定义
	  * @param taskId
	  * @return
	  * @throws Exception
	  */
	  private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(  
	            String taskId){  
	        // 取得流程定义  
	        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)  
	                .getDeployedProcessDefinition(findTaskById(taskId)  
	                        .getProcessDefinitionId());  
	        if (processDefinition == null) {  
	            //throw new Exception("流程定义未找到!");  
	        }  
	        return processDefinition;  
	    }
	  
	  /**
	   * 根据任务ID获得任务实例 
	   * @param taskId
	   * @return
	   * @throws Exception
	   */
	  private TaskEntity findTaskById(String taskId){  
	        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(  
	                taskId).singleResult();  
	        if (task == null) {  
	            //throw new Exception("任务实例未找到!");  
	        }  
	        return task;  
	    }  
	  
	  /**
	   * 根据流程实例ID获取当前任务ID
	   * @param processInstanceId
	   * @return
	   */
	  private String findTaskIdByPId(String processInstanceId){
		  Task singleResult = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		  return singleResult.getId();
	  }
	  
	  /**
	   * 审批通过(驳回直接跳回功能需后续扩展) 
	   * @param taskId
	   * @param variables
	   * @param activityId
	   * @throws Exception
	   */
	   private void commitProcess(String taskId, Map<String, Object> variables,  
	            String activityId){  
	        if (variables == null) {  
	            variables = new HashMap<String, Object>();  
	        }  
	        // 跳转节点为空，默认提交操作  
	        if (StringUtils.isEmpty(activityId)) {  
	            taskService.complete(taskId, variables);  
	        } else {// 流程转向操作  
	            turnTransition(taskId, activityId, variables);  
	        }  
	    } 
	   
	   /**
	    * 流程转向操作
	    * @param taskId
	    * @param activityId
	    * @param variables
	    * @throws Exception
	    */
	   private void turnTransition(String taskId, String activityId,  
	            Map<String, Object> variables){  
	        // 当前节点  
	        ActivityImpl currActivity = findActivitiImpl(taskId, null);  
	        // 清空当前流向  
	        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);  
	        // 创建新流向  
	        TransitionImpl newTransition = currActivity.createOutgoingTransition();  
	        // 目标节点  
	        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);  
	        // 设置新流向的目标节点  
	        newTransition.setDestination(pointActivity);  
	        // 执行转向任务  
	        taskService.complete(taskId, variables);  
	        // 删除目标节点新流入  
	        pointActivity.getIncomingTransitions().remove(newTransition);  
	        // 还原以前流向  
	        restoreTransition(currActivity, oriPvmTransitionList);  
	    }
	   
	   /**
	    *  还原指定活动节点流向 
	    * @param activityImpl
	    * @param oriPvmTransitionList
	    */
	   private void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {  
	        // 清空现有流向  
	        List<PvmTransition> pvmTransitionList = activityImpl  
	                .getOutgoingTransitions();  
	        pvmTransitionList.clear();  
	        // 还原以前流向  
	        for (PvmTransition pvmTransition : oriPvmTransitionList) {  
	            pvmTransitionList.add(pvmTransition);  
	        }  
	    }  
	   
	   /**
	    * 清空指定活动节点流向 
	    * @param activityImpl
	    * @return
	    */
	   private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {  
	        // 存储当前节点所有流向临时变量  
	        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();  
	        // 获取当前节点所有流向，存储到临时变量，然后清空  
	        List<PvmTransition> pvmTransitionList = activityImpl  
	                .getOutgoingTransitions();  
	        for (PvmTransition pvmTransition : pvmTransitionList) {  
	            oriPvmTransitionList.add(pvmTransition);  
	        }  
	        pvmTransitionList.clear();  
	        return oriPvmTransitionList;  
	    }  
	   
	   @Override
	   public Date checkProcessEnd(String procId){
	        HistoryService historyService = processEngine.getHistoryService();
	        HistoricProcessInstance singleResult = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
	        return singleResult.getEndTime();
       }
	   
}
