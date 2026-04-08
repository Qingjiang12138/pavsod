package com.pavsod.pavsodbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotBlank(message = "电话号码不能为空")
    @NotNull(message = "电话号码不能为空")
    private String phone;

    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String password;
}
