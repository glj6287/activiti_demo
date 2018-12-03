package com.yoland.activiti.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.yoland.activiti.pojo.ActIdGroup;
import com.yoland.activiti.pojo.ActIdUser;
import com.yoland.activiti.pojo.ActIdUserVo;
import com.yoland.activiti.service.ActivitiGroupAndUserService;
import com.yoland.framework.pojo.ResponseEntity;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * group 组/角色的管理
 * user的管理
 * ship 二者之间的关系维护管理
 * @author hywin
 *
 */
@Service
@Transactional
public class ActivitiGroupAndUserServiceImpl implements ActivitiGroupAndUserService {
	
	private static Logger logger = LoggerFactory.getLogger(ActivitiGroupAndUserServiceImpl.class);
	
	@Autowired
	private ProcessEngine processEngine;//流程引擎
      
	@Override
	public ResponseEntity<List<ActIdGroup>> getGroupByCondition(ActIdGroup actIdGroup) {
		actIdGroup = pagePagramToActPage(actIdGroup);
   		int pageIndex = actIdGroup.getPage();//当前第几页
   		int pageSize = actIdGroup.getPageSize();
		//判断ID是否为空
		String groupId = actIdGroup.getId();
		//判断name是否为空
		String name = actIdGroup.getName();//模糊查询
		//初始化接受
		List<Group> listPage = null;
		long count = 0;
		try {
			if(StringUtils.isEmpty(groupId) && StringUtils.isEmpty(name)){
				listPage = processEngine.getIdentityService().createGroupQuery().listPage(pageIndex, pageSize);
				count = processEngine.getIdentityService().createGroupQuery().count();
			}
			if(!StringUtils.isEmpty(groupId) && StringUtils.isEmpty(name)){
				listPage = processEngine.getIdentityService().createGroupQuery().groupId(groupId).listPage(pageIndex, pageSize);
				count = processEngine.getIdentityService().createGroupQuery().groupId(groupId).count();
			}
			if(StringUtils.isEmpty(groupId) && !StringUtils.isEmpty(name)){
				listPage = processEngine.getIdentityService().createGroupQuery().groupNameLike(name).listPage(pageIndex, pageSize);
				count = processEngine.getIdentityService().createGroupQuery().groupNameLike(name).count();
			}
			if(!StringUtils.isEmpty(groupId) && !StringUtils.isEmpty(name)){
				listPage = processEngine.getIdentityService().createGroupQuery().groupId(groupId).groupNameLike(name).listPage(pageIndex, pageSize);
				count = processEngine.getIdentityService().createGroupQuery().groupId(groupId).groupNameLike(name).count();
			}
			//处理返回结果
			List<ActIdGroup> listRet = new ArrayList<ActIdGroup>();
			if(listPage!=null && listPage.size()>0){
				for (Group group : listPage) {
					ActIdGroup entity = new ActIdGroup();
					entity.setId(group.getId());
					entity.setName(group.getName());
					listRet.add(entity);
				}
			}
			ResponseEntity<List<ActIdGroup>> responseEntity = new ResponseEntity<List<ActIdGroup>>();
			responseEntity.setData(listRet);
			responseEntity.setCount((int)count);
			return responseEntity;
		} catch (Exception e) {
			throw new RuntimeException("条件查询用户组异常");
		}
	}

	@Override
	public ResponseEntity<Object> batchCreateMembership(ActIdUserVo actIdUserVo) {
		logger.info("批量绑定用户开始");
		List<ActIdUser> actIdUserList = actIdUserVo.getActIdUserList();
		if(actIdUserList==null){
			 throw new IllegalArgumentException("用户id集合不能为空!");
		}
		String groupId = actIdUserVo.getGroupId();
		if(StringUtils.isEmpty(groupId)){
		     throw new IllegalArgumentException("组id不能为空!");
	    }
		try {
			//如果userID为空则解除该组下的所有绑定-->先解除组ID下的所有用户
			//if(actIdUserList.size()==0){
				//如果组下面有用户
				List<User> userList = processEngine.getIdentityService().createUserQuery().memberOfGroup(groupId).list();
				if(userList!=null && userList.size()>0){
					for (User user : userList) {
						String userId = user.getId();
						//把组下的用户都删除
						processEngine.getIdentityService().deleteMembership(userId, groupId);
					}
					//return new ResponseEntity<Object>() ;
				}
			//}
			//先确定group是否存在数据库
			Group group = processEngine.getIdentityService().createGroupQuery().groupId(groupId).singleResult();
			if(StringUtils.isEmpty(group.getId())){
				throw new IllegalArgumentException("组id不存在activiti库中!");
			}
			for (ActIdUser actIdUser : actIdUserList) {
				//确保用户id不能为空
				String actIdUserId = actIdUser.getId();
				if(!StringUtils.isEmpty(actIdUserId)){
					//判断传入的actIdUser是否存在activiti - user表中
					User user = processEngine.getIdentityService().createUserQuery().userId(actIdUserId).singleResult();
					//如果不在表中 则插入一条记录
					if(user==null){
						  User curUser = processEngine.getIdentityService().newUser("");//userId  
						  //user.setId(null);//自动生成  
						  curUser.setId(actIdUserId);
						  curUser.setFirstName(actIdUser.getFirst());
						  curUser.setLastName(actIdUser.getLast());
						  curUser.setEmail(actIdUser.getEmail());
						  processEngine.getIdentityService().saveUser(curUser);
					}
					//先判断二者是否绑定过关系
					List<Group> listCheck = processEngine.getIdentityService().createGroupQuery().groupMember(actIdUserId).list();
					//一初始化就删除了所有关系
					boolean flag = true;
					if(listCheck!=null && listCheck.size()>0){
						for (Group groupCheck : listCheck) {
							//当该用户不在这个组内 再绑定
							if(groupCheck.getId().equals(groupId)){
								flag=false;
							}
						}
					}
					if(flag){
						//绑定组和用户的关系
						processEngine.getIdentityService().createMembership(actIdUserId, groupId); 
					}
				}
			}
			logger.info("批量绑定用户结束");
			return new ResponseEntity<Object>();
		
		} catch (Exception e) {
			throw new RuntimeException("批量绑定用户到组角色异常");
		}
	}

	@Override
	public ResponseEntity<List<ActIdUser>> getActIdUserByGroupId(String groupId) {
		if(StringUtils.isEmpty(groupId)){
		     throw new IllegalArgumentException("组id不能为空!");
	    }
	   	try {
	   		  List<User> list = processEngine.getIdentityService().createUserQuery().memberOfGroup(groupId).list();
			  List<ActIdUser> listRet = new ArrayList<ActIdUser>();
	   		  for (User user : list) {
	   			  ActIdUser entity = new ActIdUser();
	   			  entity.setId(user.getId());
		   		  entity.setFirst(user.getFirstName());
		   	      entity.setLast(user.getLastName());
		   	      entity.setEmail(user.getEmail());
	   			  listRet.add(entity);
				  }
	   		  return new ResponseEntity<List<ActIdUser>>(listRet);
	   	 } catch (Exception e) {
   		  throw new RuntimeException("查询用户组下的用户异常");
		  }
	}
	
    @Override
     public ResponseEntity<List<ActIdGroup>> getActIdGroupByUserId(String userId){
  	  if(StringUtils.isEmpty(userId)){
		     throw new IllegalArgumentException("用户id不能为空!");
	      }
  	  try {
  		  List<Group> list = processEngine.getIdentityService().createGroupQuery().groupMember(userId).list();
  		  List<ActIdGroup> listRet = new ArrayList<ActIdGroup>();
  		  if(list!=null && list.size()>0){
	  		  for (Group groupTemp : list) {
	  			ActIdGroup entity = new ActIdGroup();
	  			entity.setId(groupTemp.getId());
	  			entity.setName(groupTemp.getName());
	  			listRet.add(entity);
			  }
  		  }
  		  ResponseEntity<List<ActIdGroup>> responseEntity = new ResponseEntity<List<ActIdGroup>>();
  		  responseEntity.setData(listRet);
  		  return responseEntity;
		  } catch (Exception e) {
			  throw new RuntimeException("查询用户所属的用户组异常");
		  }
     }

 	/**
  	 * 将前端分页传过来变为acttiviti分页需要
  	 * @param requestTask
  	 * @return
  	 */
  	public ActIdGroup pagePagramToActPage(ActIdGroup actIdGroup){
  		 //默认分页参数 0-10   10-20  30-40  40-50
  		 Integer pageIndex = actIdGroup.getPage()-1;//当前第几页
    		 Integer pageSize = actIdGroup.getPageSize();
    		 Integer pageIndexRet = pageIndex*pageSize;
    		 //如果默认第一页,不是第一页
    		 if(pageIndex!=0){
    			pageSize=pageSize+actIdGroup.getPageSize()*pageIndex;
    		 }
    		 //重新赋值
    		 actIdGroup.setPage(pageIndexRet);
    		 actIdGroup.setPageSize(pageSize);
    		return actIdGroup;
  	}

}
