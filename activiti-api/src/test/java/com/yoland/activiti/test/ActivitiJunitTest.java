package com.yoland.activiti.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注意 坑：排他网关有个默认的选项  default flow , 
 *  当 default flow 设定后  就不用设置表达式了, 
 *  如果所有的条件都不通过 就会执行默认的流程
 * @author hywin
 *
 */
//告诉spring怎样执行
@RunWith(SpringJUnit4ClassRunner.class)
//来标明是web应用测试
@WebAppConfiguration
@ContextConfiguration(locations={"classpath*:applicationContext.xml"})
//-->配置文件移动到了api
//声明一个事务管理 每个单元测试都进行事务回滚 无论成功与否
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class ActivitiJunitTest {
	
	@Autowired
    private ProcessEngine processEngine;//流程引擎对象
    @Autowired
    private RepositoryService repositoryService;//工作流仓储服务
    @Autowired
    private RuntimeService runtimeService;//工作流运行服务
    @Autowired
    private TaskService taskService;//工作流任务服务
    @Autowired
    private HistoryService historyService;//工作流历史数据服务
	
	/**部署流程定义+启动流程实例*/  
	@Test  
	public void deployementAndStartProcess(){  
	   //InputStream inputStreamBpmn = this.getClass().getResourceAsStream("exclusiveGateWay.bpmn");  
	   //InputStream inputStreampng = this.getClass().getResourceAsStream("exclusiveGateWay.png");  
	    //部署流程定义  
	    Deployment deployment = processEngine.getRepositoryService()  
                .createDeployment()//创建部署对象  
                .addClasspathResource("activiti/test.bpmn")//("audit.bpmn", inputStreamBpmn)//部署加载资源文件  
                .addClasspathResource("activiti/test.png")//("audit.png", inputStreampng)//  
                .deploy();  
	    System.out.println("部署ID："+deployment.getId());  
	    //启动流程实例  
	    ProcessInstance pi = processEngine.getRuntimeService()//  
	                        .startProcessInstanceByKey("test");//使用流程定义的key的最新版本启动流程  
	    System.out.println("流程实例ID："+pi.getId());  
	    System.out.println("流程定义的ID："+pi.getProcessDefinitionId());  
	}  
	
	/**查询我的个人任务*/  
	//@Test  
	public void findPersonalTaskList(){  
	    //任务办理人  
	    String assignee = "王小五";  
	    List<Task> list = processEngine.getTaskService()//  
	                    .createTaskQuery()//  
	                    .taskAssignee(assignee)//个人任务的查询  
	                    .list();  
	    if(list!=null && list.size()>0){  
	        for(Task task:list){  
	            System.out.println("任务ID："+task.getId());  
	            System.out.println("任务的办理人："+task.getAssignee());  
	            System.out.println("任务名称："+task.getName());  
	            System.out.println("任务的创建时间："+task.getCreateTime());  
	            System.out.println("流程实例ID："+task.getProcessInstanceId());  
	            System.out.println("#######################################");  
	        }  
	    }  
	}  
	
	/**完成任务*/  
	//@Test  
	public void completeTask(){  
	    //任务ID  
	    String taskId = "97508";  
	    //完成任务的同时，设置流程变量，让流程变量判断连线该如何执行  
	    Map<String, Object> variables = new HashMap<String, Object>();  
	    variables.put("money", 400);  
	    processEngine.getTaskService()//  
	                    .complete(taskId,variables);  
	    System.out.println("完成任务："+taskId);  
	}  

}
