package com.pavsod.pavsodbackend.controller;


import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.service.ResultService;
import common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class ResultController {

    @Autowired
    ResultService resultService;

    @GetMapping("/detect/result")
    public Result getDetectResult(@RequestParam String userId,@RequestParam String videoId){
        log.info("接收到用户{}的查询视频{}结果的请求",userId,videoId);
        Long userid = Long.valueOf(userId);
        Long videoid = Long.valueOf(videoId);

        Task task = resultService.getDetectResult(userid, videoid);
        Map<String, String> res = new HashMap<>();

        String original_video_url = resultService.getOriginalVideoUrlById(task.getOriginal_video_id());
        String saliency_video_url = resultService.getSaliencyVideoUrlById(task.getSalient_video_id());

        res.put("originalUrl", original_video_url);
        res.put("saliencyUrl", saliency_video_url);

        return Result.success(res);
    }

    @GetMapping("/detect/ai")
    public Result getAi(@RequestParam String userId, @RequestParam String video_id){
        log.info("接收到用户{}的查询视频{}ai评价的请求",userId,video_id);
        Long userid = Long.valueOf(userId);
        Long videoId = Long.valueOf(video_id);

        String ai_suggest = resultService.getAiSuggest(userid, videoId);
        Map<String, String> map = new HashMap<>();

        map.put("ai_suggestion", Objects.requireNonNullElse(ai_suggest, "暂无ai评价"));
        return Result.success(map);
    }

}
