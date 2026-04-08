package com.pavsod.pavsodbackend.exception;

import common.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RegisterExceptionHandler {

    /**
     * 处理 @Validated 校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        // 获取所有错误信息
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        String errorMsg = "请检查传递参数";

        return Result.error(400, errorMsg, errors);
    }

    /**
     * 处理数据库唯一约束冲突（手机号已存在）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        // 判断是哪个字段冲突
        System.out.println("异常："+e.getMessage());
        String message = e.getMessage();

        if (message.contains("user.phone")) {
            return Result.error(409, "手机号已被注册");
        }
        if (message.contains("user.username")) {
            return Result.error(409, "用户名已存在");
        }

        return Result.error(409, "数据已存在");
    }
}
