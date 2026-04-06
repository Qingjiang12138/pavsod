package com.pavsod.pavsodbackend.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("salient_video")
public class Salient_video {
    private Long salient_video_id;
    private Long original_video_id;
    private String video_name;
    private Integer duration;
    private Long video_size;
    private String video_url;
    private String video_cover;
}
