package com.pavsod.pavsodbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@RestController
@MapperScan("com.pavsod.pavsodbackend.mapper")
public class PavsodBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PavsodBackendApplication.class, args);
    }

    @RequestMapping("hello")
    public String hello()
    {
        return "hello world";
    }

    @PostMapping
    public String save(@RequestBody Map<String, String> map){
        System.out.println(map.toString());
        return "POST请求成功";
    }
}
