package com.yoland.activiti.enums;

/**
 * 工作流
 * userTask Key
 * @author hywin
 *
 */
public enum ActivitiUsertaskEnum {
	
	HYGJCREDITUSERTASK("usertask","hygjCreditUsertask"),//高金信审节点
	HYGJCASHCONFIRMUSERTASK("usertask","hygjCashConfirmUsertask"),//高进头寸确认节点
	LOANPENDINGUSERTASK ("usertask","loanpendingUsertask"),//待还款节点
	REPAYMENTUSERTASK ("usertask","repaymentUsertask "),//还款中节点
	HYGJPLEDGETHAWUSERTASK("usertask","hygjPledgeThawUsertask"),//高金质押解冻节点
	HUMANINTERVENTIONUSERTASK("usertask","humanInterventionUsertask"),//人工介入节点
	HUMANLOANUSERTASK("usertask","humanLoanUsertask"),//人工放款节点
	ENDUSERTASK("usertask","endUsertask");//结束key
	
    private String code;
    private String name;

    ActivitiUsertaskEnum(String code, String name) {
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
