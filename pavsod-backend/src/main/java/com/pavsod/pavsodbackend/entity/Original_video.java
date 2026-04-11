package com.pavsod.pavsodbackend.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("original_video")
public class Original_video {
    private Long video_id;
    private String video_name;
    private String video_type;
    private Integer duration;
    private Long video_size;
    private String video_url;
    private String video_cover;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime upload_time;
}
