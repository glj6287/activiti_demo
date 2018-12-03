package com.yoland.activiti.listener;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务监听器
 * 实现TaskListener接口
 * @author hywin
 *
 */
public class TaskListenerDemo implements Serializable, TaskListener {
	
	private static Logger logger = LoggerFactory.getLogger(TaskListenerDemo.class);

	private static final long serialVersionUID = 1133417160514782731L;
	private Expression arg;  
	  
    public Expression getArg() {  
        return arg;  
    }  
  
    public void setArg(Expression arg) {  
        this.arg = arg;  
    }  
  
    @Override
    public void notify(DelegateTask delegateTask) {
    	delegateTask.setVariable("variableName", "value");
    	delegateTask.setAssignee("zhaoming");
        logger.info("任务监听器:" + arg.getValue(delegateTask));
    }  
}
