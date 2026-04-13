package com.pavsod.pavsodbackend.service.impl;

import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.mapper.ResultMapper;
import com.pavsod.pavsodbackend.service.ResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    ResultMapper resultMapper;

    @Override
    public Task getDetectResult(Long userId, Long videoId) {

        Task task = resultMapper.getTaskByUserIdAndOriginalVideoId(userId, videoId);
        return task;
    }

    @Override
    public String getAiSuggest(Long userId, Long videoId) {
        String ai_suggest = resultMapper.getAiSuggest(userId,videoId);
        return ai_suggest;
    }

    @Override
    public String getOriginalVideoUrlById(Long originalVideoId) {
        String originalVideoUrl = resultMapper.getOriginalVideoUrlById(originalVideoId);
        return originalVideoUrl;
    }

    @Override
    public String getSaliencyVideoUrlById(Long salientVideoId) {
        String saliencyVideoUrl = resultMapper.getSaliencyVideoUrlById(salientVideoId);
        return saliencyVideoUrl;
    }
}
