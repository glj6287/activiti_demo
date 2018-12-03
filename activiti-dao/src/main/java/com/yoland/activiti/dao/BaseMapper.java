package com.yoland.activiti.dao;

import java.io.Serializable;

/**
 * <br>
 *
 * @Author: guanlj
 * @version 1.0
 * @date: 2018/6/3  3:34
 */
public interface BaseMapper<T, ID extends Serializable> {

	int deleteByPrimaryKey(ID id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(ID id);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}