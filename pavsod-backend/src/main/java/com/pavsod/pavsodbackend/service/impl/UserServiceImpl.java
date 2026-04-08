package com.pavsod.pavsodbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.UserMapper;
import com.pavsod.pavsodbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    //用户注册
    @Override
    public void userRegister(User user) {

        //密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(user.getPassword());  // 加密


        user.setPassword(encryptedPassword);
        System.out.println("加密密码：" + encryptedPassword);

        userMapper.insert(user);
    }
}
