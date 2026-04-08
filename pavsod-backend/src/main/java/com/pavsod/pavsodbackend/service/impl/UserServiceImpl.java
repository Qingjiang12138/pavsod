package com.pavsod.pavsodbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;
import com.pavsod.pavsodbackend.dto.UserRegisterDTO;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.UserMapper;
import com.pavsod.pavsodbackend.pojo.LoginInfo;
import com.pavsod.pavsodbackend.service.UserService;
import com.pavsod.pavsodbackend.utils.JWTUtil;
import com.pavsod.pavsodbackend.utils.MD5Util;
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
    public void userRegister(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());

        String encryptedPassword = MD5Util.encrypt(dto.getPassword());
        user.setPassword(encryptedPassword);

        System.out.println("加密密码：" + encryptedPassword);

        userMapper.insert(user);
    }

    @Override
    public LoginInfo userLogin(UserLoginDTO dto){
        //根据用户名和密码查询员工信息
        String encryptedPassword = MD5Util.encrypt(dto.getPassword());
        dto.setPassword(encryptedPassword);

        User user = userMapper.selectByPhoneAndPassword(dto);

        //判断是否存在这个员工，如果存在，组装登录成功的信息
        if(user != null){
            String token = JWTUtil.getToken(dto);

            return new LoginInfo(user.getId(), user.getUsername(),
                    user.getPhone(), user.getAvatar(), token);
        }

        //不存在，返回null
        return null;
    }
}
