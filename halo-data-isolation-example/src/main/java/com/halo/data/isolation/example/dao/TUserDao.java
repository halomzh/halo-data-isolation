package com.halo.data.isolation.example.dao;

import com.halo.data.isolation.example.entity.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (TUser)表数据库访问层
 *
 * @author shoufeng
 * @since 2021-03-28 00:03:06
 */

@Mapper
public interface TUserDao {

	@Select("select * from t_user limit 0,10")
	List<TUser> selectAll();
}

