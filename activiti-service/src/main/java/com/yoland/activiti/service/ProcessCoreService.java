package com.yoland.activiti.service;

import java.util.Date;

/**
 * 流程中止 等相关操作
 * @author hywin
 *
 */
public interface ProcessCoreService {

	void endProcess(String taskId) throws Exception;

	void endProcessByPid(String processInstanceId);

	Date checkProcessEnd(String procId);

}
