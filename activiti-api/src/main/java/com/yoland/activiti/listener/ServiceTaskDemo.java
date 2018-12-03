package com.yoland.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
//import org.slf4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTaskDemo implements JavaDelegate{

	private static Logger logger = LoggerFactory.getLogger(ExectuionListenerDemo.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Thread.sleep(10000);
		logger.info(execution.getVariables()+"Variables");
		
		execution.setVariable("task1", "I am task1");
		logger.info("I am task1 ");
		
		execution.setVariable("msg", "不通过");
		logger.info((String) execution.getVariable("msg"));
		
	}
	
	
}
