package com.pavsod.pavsodbackend;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.UserMapper;
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

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }

    @Test
    void contextService(){
        List<User> userList = userService.list();
        userList.forEach(System.out::println);
    }

    @Test
    void contextLoads() {
    }

}
