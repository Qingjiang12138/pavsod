package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id, username, phone, avatar from user where phone = #{phone} and password = #{password}")
    User selectByPhoneAndPassword(UserLoginDTO dto);

    @Select("SELECT COUNT(*) FROM task " +
            "WHERE user_id = #{userId} " +
            "AND create_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')")
    Integer countCurrentMonthDetectionCount(@Param("userId") Long userId);


    @Select("SELECT SUM(ov.duration) FROM task t " +
            "INNER JOIN original_video ov ON t.original_video_id = ov.video_id " +
            "WHERE t.user_id = #{userId}")
    Integer sumTotalDetectionDuration(@Param("userId") Long userId);

    @Select("SELECT id, username, password, avatar, email, phone, " +
            "detection_count, total_processed_videos, total_2d_videos, " +
            "total_3d_videos, current_storage, max_storage, created_at " +
            "FROM user WHERE id = #{userId}")
    User selectByUserId(Long id);

    @Select("SELECT DATE(create_at) as date, COUNT(*) as count " +
            "FROM task " +
            "WHERE user_id = #{userId} " +
            "AND create_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_at) " +
            "ORDER BY DATE(create_at)")
    List<Map<String, Object>> selectLast7DaysTaskCount(@Param("userId") Long userId);

    @Select("SELECT * FROM task " +
            "WHERE user_id = #{userId} " +
            "ORDER BY create_at DESC, task_id DESC " +
            "LIMIT 5")
    List<Task> select5LastRecords(@Param("userId") Long userId);
}
