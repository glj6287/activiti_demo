package com.yoland.activiti.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.yoland.activiti.service.GroupAndUserService;
import com.yoland.framework.pojo.ResponseEntity;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内部 接口
 * group 组/角色的管理
 * user的管理
 * ship 二者之间的关系维护管理
 * @author hywin
 *
 */
@Service
@Transactional
public class GroupAndUserServiceImpl implements GroupAndUserService {
	
    private final Logger logger = LogManager.getLogger(this.getClass());
	
	 @Autowired
	 private ProcessEngine processEngine;//流程引擎
	 
	/**
	 * 添加组用户组
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public ResponseEntity saveGroup(String id , String name) {
		logger.info("添加组用户组开始");
	    if(StringUtils.isEmpty(id) || StringUtils.isEmpty(name)){
		     throw new IllegalArgumentException("组id或名称不能为空!");
	    }
		try {
			  // 得到身份服务组件实例  
			  IdentityService identityService = processEngine.getIdentityService(); 
			  // 调用newGroup方法创建Group实例  
			  Group group = identityService.newGroup("");  //groupId 给以给任务多个角色
			  //为了使用Activiti自己的ID生成策略  
			  //group.setId(null); 
			  group.setId(id);
			  group.setName(name);  
			  //将Group保存到数据库  
			  identityService.saveGroup(group); 
			  return new ResponseEntity();
		} catch (Exception e) {
			throw new RuntimeException("添加组角色异常");
		}
		
	}

     /** 
      *  
      * 添加用户 
      *  
      * */  
	  @Override
	  @SuppressWarnings("rawtypes")
	  public ResponseEntity addUser(String userId,String firstName ,String lastName){
		  if(StringUtils.isEmpty(userId)){
			  throw new IllegalArgumentException("用户id不能为空!");
		  }
		  try {
			  User user = processEngine.getIdentityService().newUser("");//userId  
			  //user.setId(null);//自动生成  
			  user.setId(userId);
			  user.setFirstName(firstName);
			  user.setLastName(lastName);
			  processEngine.getIdentityService().saveUser(user);
			  return new ResponseEntity();
			} catch (Exception e) {
				throw new RuntimeException("添加用户异常");
			}
	  }
      
     /** 
      *  
      * 绑定用户与用户组的关系 
      *  
      * */  
      @Override
	  @SuppressWarnings("rawtypes")
      public ResponseEntity createMembership(String userId, String groupId){
    	  if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(groupId)){
			  throw new IllegalArgumentException("用户id或组id不能为空!");
		  }
    	  try {
    		  processEngine.getIdentityService().createMembership(userId, groupId); 
    		  return new ResponseEntity();
			} catch (Exception e) {
				throw new RuntimeException("绑定用户与用户组的关系异常");
		  }
      }

     /** 
      *  
      * 解除用户组与用户的绑定关系 
      *  
      * */  
      @Override
      @SuppressWarnings("rawtypes")
      public ResponseEntity deleteMembership(String userId, String groupId){
    	  if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(groupId)){
			  throw new IllegalArgumentException("用户id或组id不能为空!");
		  }
    	  try {
    		  processEngine.getIdentityService().deleteMembership(userId, groupId);
    		  return new ResponseEntity(); 
		  } catch (Exception e) {
			  throw new RuntimeException("解除用户与用户组的关系异常");
		  }
      }

     /** 
      *  
      * 查询用户组下的用户 
      *  
      * */  
      @Override
      @SuppressWarnings({ "unchecked", "rawtypes" })
      public ResponseEntity getUserByGroupId(String groupId){
    	  if(StringUtils.isEmpty(groupId)){
 		     throw new IllegalArgumentException("组id不能为空!");
 	      }
    	  try {
    		  List<User> list = processEngine.getIdentityService().createUserQuery().memberOfGroup(groupId).list();
		      List<Map<String,Object>> listRet = new ArrayList<Map<String,Object>>();
    		  for (User user : list) {
    			  Map<String,Object> map = new HashMap<String, Object>();
    			  map.put("userId", user.getId());
    			  map.put("userName", user.getFirstName()+user.getLastName());
    			  listRet.add(map);
			  }
    		  return new ResponseEntity(listRet);
    	  } catch (Exception e) {
    		  throw new RuntimeException("查询用户组下的用户异常");
		  }
      }
       
     /** 
      *  
      * 查询用户所属的用户组 
      *  
      * */
      @Override
      @SuppressWarnings({ "rawtypes", "unchecked" })
       public ResponseEntity getGroupByUserId(String userId){
    	  if(StringUtils.isEmpty(userId)){
  		     throw new IllegalArgumentException("用户id不能为空!");
  	      }
    	  try {
    		  Group group  = processEngine.getIdentityService()
    				  .createGroupQuery().groupMember(userId).singleResult();  
    		  Map<String,Object> map = new HashMap<String, Object>();
			  map.put("groupId", group.getId());
			  map.put("groupName", group.getName());
    		  return new ResponseEntity(map);
		  } catch (Exception e) {
			  throw new RuntimeException("查询用户所属的用户组异常");
		  }
       }

}
