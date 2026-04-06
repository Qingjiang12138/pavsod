package com.pavsod.pavsodbackend.controller;

import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.service.UserService;
import common.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

//    @PostMapping
//    public Result save(@RequestBody User user){
//        userService.save(user);
//        return Result.success();
//    }

    @PostMapping public Result print(@RequestBody Map<String, Object> data){
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
