package com.yoland.activiti.service;

import java.util.List;

import com.yoland.activiti.pojo.ActIdGroup;
import com.yoland.activiti.pojo.ActIdUser;
import com.yoland.activiti.pojo.ActIdUserVo;
import com.yoland.framework.pojo.ResponseEntity;

/**
 * 1.组查询: 动态查询+分页
   2.用户查询: 根据组ID查询用户列表
   3.用户分组更新: 组ID, 用户信息(批操作)	
 * @author hywin
 *
 */
public interface ActivitiGroupAndUserService {
	
	/**
	 * 分页条件组查询
	 * @param id
	 * @param name
	 * @param pageIndexStr
	 * @param pageSizeStr
	 * @return
	 */
	ResponseEntity<List<ActIdGroup>> getGroupByCondition(ActIdGroup actIdGroup);
	
	/**
	 * 批量更新绑定用户到组
	 * @param ActIdUserVo
	 * @return
	 */
	ResponseEntity<Object> batchCreateMembership(ActIdUserVo actIdUserVo);
	
	/**
	 * 根据组Id获取用户-->新返回值
	 * @param groupId
	 * @return
	 */
	ResponseEntity<List<ActIdUser>> getActIdUserByGroupId(String groupId);

	/**
	 * 根据用户Id得到组信息
	 * @param userId
	 * @return
	 */
	ResponseEntity<List<ActIdGroup>> getActIdGroupByUserId(String userId);

	

}
