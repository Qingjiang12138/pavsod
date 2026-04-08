package com.pavsod.pavsodbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pavsod.pavsodbackend.dto.UserRegisterDTO;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

public interface UserService extends IService<User> {

    public void userRegister(UserRegisterDTO dto);
}
