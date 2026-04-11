package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.entity.Original_video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OriginalVideoMapper extends BaseMapper<Original_video> {

    @Select("select * from original_video where video_id = #{originalVideoId}")
    Original_video selectVideoById(Long originalVideoId);
}
