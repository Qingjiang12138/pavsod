package com.pavsod.pavsodbackend.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordInfo {
    private Long video_id;
    private String video_cover;
    private String video_name;
    private String video_url;
    private Integer video_status;
    private LocalDateTime task_create_at;
    private Integer video_duration;
}
