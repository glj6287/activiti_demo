package com.yoland.activiti.enums;

/**
 * 工作流
 * group Key
 * @author hywin
 *
 */
public enum ActivitiGroupEnum {
	
	HYGJCREDITGROUP("group","hygjCreditGroup"),//高金信审人员组
	FINANCECOMMONGROUP("group","financeCommonGroup"),//财务通用组
	SYS("personal","sys");//系统默认用户
	
	

    private String code;
    private String name;

    ActivitiGroupEnum(String code, String name) {
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
