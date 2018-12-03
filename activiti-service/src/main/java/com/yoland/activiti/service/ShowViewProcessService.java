package com.yoland.activiti.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public interface ShowViewProcessService {

	String showView(String deploymentId, String diagramResourceName,
			HttpServletResponse response) throws Exception;

	void viewPng() throws IOException;

	byte[] generateImage(String processInstanceId);

	String getCurActiviti(String procInstanceId);

	byte[] test(String processInstanceId);

}
