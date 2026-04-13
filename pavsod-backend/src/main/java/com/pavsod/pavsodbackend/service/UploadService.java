package com.pavsod.pavsodbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    Long upload(Long userId, Integer frame, MultipartFile file, String video_type, Integer duration) throws Exception;
}
