package com.pavsod.pavsodbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfo {
    private Long userId;
    private String username;
    private String phone;
    private String avatar;
    private String token;
}
