package com.cms.cmssetting.mapper;

import com.cms.cmssetting.entity.User;
import com.cms.cmssetting.entity.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
* Created by Momf Generator on 2018/10/18
*/
public interface UserMapper {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);
}