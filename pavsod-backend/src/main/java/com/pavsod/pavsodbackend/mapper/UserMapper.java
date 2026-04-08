package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
