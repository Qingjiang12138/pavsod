package com.pavsod.pavsodbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.UserMapper;
import com.pavsod.pavsodbackend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
