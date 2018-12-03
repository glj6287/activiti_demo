package com.yoland.common.constant;

/**
 * @author wuyouyang
 * @time 2017年5月2日10:48:54
 */
public interface Constants {

	/**
	 * OOO系统
	 * OOO△△xxx
	 * 中间三位△△预留，默认为00
	 * 后三位xxx表示错误码
	 * 后三位中xxx，001-099预留为公司统一使用的编码
	 * 
	 * 
	 * 004 工作流平台
	 */
	public interface activiti{

		// 系统异常（未定义、未捕获、未处理的统一放这里）
//		public static final String ERROR_CODE01 = "01000001";
//		public static final String ERROR_MSG01 = "系统错误，请联系管理员!";
		
		
		public static final String ERROR_CODE01 = "004000001";
		public static final String ERROR_MSG01 = "部署失败，请联系管理员!";
		
		public static final String ERROR_CODE02 = "004000002";
		public static final String ERROR_MSG02 = "启动流程实例失败，请联系管理员!";
		
		public static final String ERROR_CODE03 = "004000003";
		public static final String ERROR_MSG03 = "添加备注失败，请联系管理员!";
		
		public static final String ERROR_CODE04 = "004000004";
		public static final String ERROR_MSG04 = "移交任务失败，此用户不属于该部门!";
		
		

	}

	
	/**
	 * 分页默认参数
	 * @author hywin
	 *
	 */
	public static class PageDefProgarm{
		public static final Integer PageIndex=0;//
		public static final Integer PageSize=10;//
	}
	
	public static class TaskStatus{
		public static final String task_status_key="status";//公用接口条件判断
		public static final String task_adopt = "01";//通过
		public static final String task_refuse = "02";//拒绝
		public static final String task_return = "03";//退回
	}

}
