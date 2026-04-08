package com.pavsod.pavsodbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@TableName("`user`")
public class User {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    private String password;

    private String avatar;

    @Email
    private String email;

    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    private String phone;

    private Integer detection_count;
    private Integer total_processed_videos;
    private Integer total_2d_videos;
    private Integer total_3d_videos;
    private Long current_storage;
    private Long max_storage;
    private Date created_at;
}
