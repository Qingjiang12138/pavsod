package com.pavsod.pavsodbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;
import com.pavsod.pavsodbackend.dto.UserRegisterDTO;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.UserMapper;
import com.pavsod.pavsodbackend.pojo.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public interface UserService extends IService<User> {

    void userRegister(UserRegisterDTO dto);

    LoginInfo userLogin(UserLoginDTO dto);

    Map<String, Object> getHomeData(Long userId);
}
