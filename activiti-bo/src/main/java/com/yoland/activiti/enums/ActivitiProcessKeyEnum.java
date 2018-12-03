package com.yoland.activiti.enums;

/**
 * 工作流
 * 启动流程实例 key  枚举
 * @author hywin
 *
 */
public enum ActivitiProcessKeyEnum {
	
	HYGJBUSINESS("key","hygjBusiness"),//高金v2流程
	HYGJBUSINESSEASY("key","hygjBusinessEasy");//高金v1简化版流程

    private String code;
    private String name;

    ActivitiProcessKeyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
