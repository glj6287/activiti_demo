package com.yoland.activiti.listener;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 *  
 * 可以使用 CLASS ,EXPRESSION,DELEGATE EXPRESSSION
 * 三种方式来创建监听器，这里使用第三种方式，其他两种方式和 
 * 在servicetask中的使用方式相同 
 *  
 * */  
public class ExectuionListenerDemo implements Serializable, ExecutionListener{

	private static Logger logger = LoggerFactory.getLogger(ExectuionListenerDemo.class);
	
	private static final long serialVersionUID = 6687595351652713696L;

	private Expression message;
	
	public Expression getMessage() {
		return message;
	}
	
	public void setMessage(Expression message) {
		this.message = message;
	}
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		logger.info("流程监听器" + message.getValue(execution));
	}

}
