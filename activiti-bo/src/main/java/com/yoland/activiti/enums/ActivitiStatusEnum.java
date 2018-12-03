package com.yoland.activiti.enums;

/**
 * 工作流
 * 流程变量 基本定义
 * @author hywin
 *
 */
public enum ActivitiStatusEnum {
	
	STATUS_T("status","T"),//通过
	STATUS_F("status","F"),//拒绝
	STATUS_C("status","C");//撤销

    private String code;
    private String name;

    ActivitiStatusEnum(String code, String name) {
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
