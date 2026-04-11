package com.pavsod.pavsodbackend.controller;

import com.pavsod.pavsodbackend.dto.UserLoginDTO;
import com.pavsod.pavsodbackend.dto.UserRegisterDTO;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.pojo.LoginInfo;
import com.pavsod.pavsodbackend.service.UserService;
import com.pavsod.pavsodbackend.utils.JWTUtil;
import common.Result;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class UserController {

    @Resource
    private UserService userService;

//    @PostMapping
//    public Result save(@RequestBody User user){
//        userService.save(user);
//        return Result.success();
//    }

    @PostMapping("/user/register")
    public Result register(@RequestBody @Validated UserRegisterDTO dto){
        userService.userRegister(dto);
        return Result.success();
    }

    @PostMapping("/user/login")
    public Result login(@RequestBody @Validated UserLoginDTO dto){
        log.info("登录：{}", dto);
        LoginInfo info = userService.userLogin(dto);

        if(info != null)
            return Result.success(info);
        else
            return Result.error("用户名或密码错误");
    }

    @GetMapping("/home/data")
    public Result getHomeData(@RequestBody Map<String, Object> map){
        Long userId = (Long)map.get("userId");
        Map<String, Object> data = userService.getHomeData(userId);


        if (!data.isEmpty())
            return Result.success(data);
        else
            return Result.error("用户不存在");
    }

    @GetMapping("/user/data")
    public Result getUserData(@RequestBody Map<String,Object> userId){
        try {
            User user = userService.getUserData((Long)userId.get("userId"));
            return Result.success(user);
        }
        catch (Exception e){
            return Result.error();
        }

    }



    //以下是学习时候的代码
    @PostMapping
    public Result print(@RequestBody Map<String, Object> data){
        System.out.println(data.get("id"));
        System.out.println(data.get("name"));

        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody User user){
        userService.updateById(user);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getOne(@PathVariable Long id){
        return Result.success(userService.getById(id));
    }

    @GetMapping
    public Result list(){
        return Result.success(userService.list());
    }

    @DeleteMapping
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

}
