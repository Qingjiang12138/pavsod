package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.entity.Original_video;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UploadMapper extends BaseMapper<Original_video> {

    @Select("select video_id from original_video where video_name = #{fileName}")
    Long selectOriginalVideoIdByName(String fileName);

    @Insert("INSERT INTO task(user_id, original_video_id, video_type, target_fps, process_count, task_status, create_at) " +
            "VALUES (#{user_id}, #{original_video_id}, #{video_type}, #{target_fps}, #{process_count}, #{task_status}, #{create_at})")
    void insertTask(Task task);
}
