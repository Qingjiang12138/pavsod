package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;
import com.pavsod.pavsodbackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id, username, phone, avatar from user where phone = #{phone} and password = #{password}")
    User selectByPhoneAndPassword(UserLoginDTO dto);
}
