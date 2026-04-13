package com.pavsod.pavsodbackend.service;


import com.pavsod.pavsodbackend.entity.Task;

public interface ResultService {

    Task getDetectResult(Long userId, Long videoId);

    String getAiSuggest(Long userid, Long videoId);

    String getOriginalVideoUrlById(Long originalVideoId);

    String getSaliencyVideoUrlById(Long salientVideoId);
}
