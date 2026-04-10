package com.pavsod.pavsodbackend.controller;


import com.pavsod.pavsodbackend.service.UploadService;
import com.pavsod.pavsodbackend.utils.AliyunOSSOperator;
import common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    //文件上传，接受用户id，希望检测之后视频的帧率，上传视频文件
    //返回保存的视频地址，检测之后视频地址
    @PostMapping("detect/upload")
    public Result upload(Long userId, Integer frame, MultipartFile file, String video_type, Integer duration) throws Exception {
        log.info("upload接收参数：{},{},{}",userId, frame, file);

        try {
            uploadService.upload(userId, frame, file, video_type, duration);
        }
        catch (Exception e){
            return  Result.error();
        }

        return Result.success();
    }
}
