package com.yoland.activiti.service;

import com.yoland.framework.pojo.ResponseEntity;

/**
 * 1.组查询: 动态查询+分页
   2.用户查询: 根据组ID查询用户列表
   3.用户分组更新: 组ID, 用户信息(批操作)	
 * @author hywin
 *
 */
public interface GroupAndUserService {
	

	/**
	 * 添加组角色
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity saveGroup(String id , String name);

	/**
	 * 添加用户
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity addUser(String userId, String firstName, String lastName);

	/**
	 * 创建用户和组的关系
	 * @param userId
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity createMembership(String userId, String groupId);

	/**
	 * 删除用户和组的关系
	 * @param userId
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity deleteMembership(String userId, String groupId);

	/**
	 * 根据组Id获取用户
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity getUserByGroupId(String groupId);

	/**
	 * 根据用户获取组
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ResponseEntity getGroupByUserId(String userId);

}
