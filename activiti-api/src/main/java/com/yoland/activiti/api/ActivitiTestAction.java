package com.yoland.activiti.api;

import com.yoland.activiti.pojo.RequestTask;
import com.yoland.activiti.service.ActivitiTaskManageService;
import com.yoland.activiti.service.ProcessCoreService;
import com.yoland.activiti.service.ShowViewProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("activiti")
public class ActivitiTestAction {
	
	@Autowired
	private ActivitiTaskManageService activitiTaskManageService;
	
	@Autowired
	private ProcessCoreService processCoreService;
	
	@Autowired
	private ShowViewProcessService showViewProcessService;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String test(HttpServletResponse response){
//		RequestTask requestTask = new RequestTask();
////		requestTask.setProcInstId("190501");
////		activitiTaskManageService.endProcessByProcInstId(requestTask);
//		requestTask.setProcdefKey(ActivitiProcessKeyEnum.HYGJBUSINESSEASY.getName());
//		requestTask.setUserId("xuyang");
//		ResponseEntity<ResponseTask> startProcessInstanceByKey = activitiTaskManageService.startProcessInstanceByKey(requestTask);
//		System.out.println(startProcessInstanceByKey);
//		
//		
		//290511
//		RequestTask requestTask2 = new  RequestTask();
//		requestTask2.setUserId("zhaoming");
//		Map<String, Object> variables = new HashMap<String, Object>();
//		variables.put("status", "F");
//		requestTask2.setVariables(variables );
//		requestTask2.setTaskId("305538");
//		ResponseEntity<ResponseTask> completeTaskByUserId = activitiTaskManageService.CompleteTaskByUserId(requestTask2);
		
//		processCoreService.checkProcessEnd("320506");
//		
//		System.out.println(completeTaskByUserId);
//		requestTask.setProcInstId("98001");
//		ResponseEntity<ResponseTask> findTaskIdByPId = activitiTaskManageService.findTaskIdByPId(requestTask);
//		System.out.println(findTaskIdByPId);
		
		
		RequestTask requestTask = new RequestTask();
		requestTask.setProcInstId("328879");
		//showViewProcessService.test("328879");
		activitiTaskManageService.currentTaskNodePng(requestTask );
		return "22";
	}

}
