package com.pavsod.pavsodbackend;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.RecordMapper;
import com.pavsod.pavsodbackend.mapper.UserMapper;
import com.pavsod.pavsodbackend.pojo.RecordInfo;
import com.pavsod.pavsodbackend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PavsodBackendApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Autowired
    private RecordMapper recordMapper;


    @Test
    void contextLoads() {
    }


}
