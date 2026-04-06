package com.pavsod.pavsodbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("`user`")
public class User {
    private Long id;
    private String username;
    private String password;
    private String avatar;
    private String email;
    private String phone;
    private Integer detection_count;
    private Integer total_processed_videos;
    private Integer total_2d_videos;
    private Integer total_3d_videos;
    private Long current_storage;
    private Long max_storage;
    private Date created_at;
}
