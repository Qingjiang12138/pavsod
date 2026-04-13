package com.pavsod.pavsodbackend.mapper;

import com.pavsod.pavsodbackend.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResultMapper {

    String getAiSuggest(Long userId, Long videoId);

    Task getTaskByUserIdAndOriginalVideoId(Long userId, Long videoId);

    String getOriginalVideoUrlById(Long originalVideoId);

    String getSaliencyVideoUrlById(Long salientVideoId);
}
