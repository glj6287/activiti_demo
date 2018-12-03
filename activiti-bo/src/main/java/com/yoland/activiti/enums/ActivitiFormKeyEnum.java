package com.yoland.activiti.enums;

/**
 * 工作流
 * form key 枚举
 * @author hywin
 *
 */
public enum ActivitiFormKeyEnum {
	
	HYGJCREDITFORMKEY("formKey","hygjCreditFormKey"),//高金信审表单
	HYGJCASHCONFIRMFORMKEY("formKey","hygjCashConfirmFormKey"),//高金头寸确认表单
	HUMANINTERVENTIONFORMKEY("formKey","humanInterventionFormKey"),//人工介入表单
	HUMANLOANFORMKEY("formKey","humanLoanFormKey"),//人工放款表单
	HYGJPLEDGETHAWFORMKEY("formKey","hygjPledgeThawFormKey");//高金质押解冻
	
	

    private String code;
    private String name;

    ActivitiFormKeyEnum(String code, String name) {
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
