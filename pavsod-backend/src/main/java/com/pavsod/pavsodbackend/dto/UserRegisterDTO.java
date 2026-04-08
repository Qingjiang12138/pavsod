package com.pavsod.pavsodbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "电话号码不能为空")
    @NotBlank(message = "电话号码不能为空")
    private String phone;

}
