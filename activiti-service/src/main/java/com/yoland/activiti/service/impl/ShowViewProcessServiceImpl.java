package com.yoland.activiti.service.impl;

import com.yoland.activiti.service.ShowViewProcessService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 辅助参考接口，不向外暴露
 * @author hywin
 *
 */
@Service
@Transactional
public class ShowViewProcessServiceImpl implements ShowViewProcessService {
	
	private static Logger logger = LoggerFactory.getLogger(ShowViewProcessServiceImpl.class);

     @Autowired
	 private ProcessEngine processEngine;//流程引擎
     @Autowired
     RepositoryService repositoryService;
     @Autowired
     ManagementService managementService;
     @Autowired
     protected RuntimeService runtimeService;
     @Autowired
     ProcessEngineConfiguration processEngineConfiguration;
//     @Autowired
//     ProcessEngineFactoryBean processEngine;
     @Autowired
     HistoryService historyService;
     @Autowired
     TaskService taskService;
     
	 /**
     * 
     * @param deploymentId
     * @param diagramResourceName
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public String showView(String deploymentId,String diagramResourceName,HttpServletResponse response)throws Exception{
		InputStream inputStream=processEngine.getRepositoryService().getResourceAsStream(deploymentId, diagramResourceName);
		OutputStream out=response.getOutputStream();
		for(int b=-1;(b=inputStream.read())!=-1;){
			out.write(b);
		}
		out.close();
		inputStream.close();
		return null;
	}
    
    /**
     * 查看流程图
     */
    @Override
    public void viewPng() throws IOException{
    	String deploymentId="28011";
    	List<String> list = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
    	//获得资源名称后缀.png  
        String resourceName = ""; 
        if(list != null && list.size()>0){  
	    	for (String name : list) {
				if(name.indexOf(".png")>=0){//返回包含该字符串的第一个字母的索引位置 
					resourceName = name;
				}
			}
    	}
        //获取输入流，输入流中存放.PNG的文件 
        InputStream resourceAsStream = processEngine.getRepositoryService().getResourceAsStream(deploymentId, resourceName);
        //将获取到的文件保存到本地  
        FileUtils.copyInputStreamToFile(resourceAsStream, new File("D:/" + resourceName));
        logger.info("文件保存成功！");
    }
    
    /**
     * 当前节点标红
     * @param processInstanceId
     * @return
     */
    @Override
    public  byte[] generateImage(String processInstanceId){  
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
      try {    
          //生成本地图片  
          File file = new File("D:/test.png");  
          FileUtils.copyInputStreamToFile(inputStream, file);  
          return IOUtils.toByteArray(inputStream);    
       } catch (Exception e) {    
           throw new RuntimeException("生成流程图异常！", e);    
       } finally {    
           IOUtils.closeQuietly(inputStream);   
       }    
  }  
    
    //核实流程是否结束
    public void checkProcessEnd(String procId){
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance singleResult = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
        logger.info(singleResult.getEndTime()+"");
    }
    
    //查询历史流程变量
    public void findHisVariablesList(){  
        String processInstanceId = "22501";  
        List<HistoricVariableInstance> list = processEngine.getHistoryService()  
                .createHistoricVariableInstanceQuery()  
                .processInstanceId(processInstanceId)  
                .list();  
        if(list != null && list.size()>0){  
            for(HistoricVariableInstance hvi:list){  
            	logger.info(hvi.getId()+"    "+hvi.getVariableName()+"   "+hvi.getValue());  
            }  
        }  
    } 
    
    
    /**
     * 获取当前节点  下一节点
     * @param procInstanceId
     */
	@Override
	@SuppressWarnings("unused")
    public String getCurActiviti(String procInstanceId){
    	String nextId = "";
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
        					nextId = ac.getId();
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

	
	
	
	@Override
	 public byte[] test(String processInstanceId){

	        //获取历史流程实例
	        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
	        //获取流程图
	        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
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
	        try {    
	            //生成本地图片  
	            File file = new File("D:/test.png");  
	            FileUtils.copyInputStreamToFile(inputStream, file);  
	            return IOUtils.toByteArray(inputStream);    
	         } catch (Exception e) {    
	             throw new RuntimeException("生成流程图异常！", e);    
	         } finally {    
	             IOUtils.closeQuietly(inputStream);   
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
}
