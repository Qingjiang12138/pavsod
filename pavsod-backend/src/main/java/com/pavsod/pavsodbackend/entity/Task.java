package com.pavsod.pavsodbackend.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("task")
public class Task {
    private Long task_id;
    private Long user_id;
    private Long original_video_id;
    private Long salient_video_id;
    private String video_type;
    private Integer target_fps;
    private Integer process_count;
    private Integer task_status;
    private Date start_time;
    private Date end_time;
    private String error_msg;
    private LocalDateTime create_at;
}
