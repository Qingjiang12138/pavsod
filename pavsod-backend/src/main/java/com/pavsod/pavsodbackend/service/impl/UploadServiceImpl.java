package com.pavsod.pavsodbackend.service.impl;

import com.pavsod.pavsodbackend.entity.Original_video;
import com.pavsod.pavsodbackend.mapper.UploadMapper;
import com.pavsod.pavsodbackend.service.UploadService;
import com.pavsod.pavsodbackend.utils.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private UploadMapper uploadMapper;

    @Override
    public String upload(Long userId, Integer frame, MultipartFile file, String video_type, Integer duration) throws Exception {

        String fileName = UUID.randomUUID() + file.getOriginalFilename();
        System.out.println("原始文件名称：" + file.getOriginalFilename());
        System.out.println("UUID之后文件名称：" + fileName);

        String video_url = aliyunOSSOperator.upload(file.getInputStream(), fileName);
        log.info("视频url：" + video_url);

        //这里插入提取视频封面的方法，并保存为一个File变量video_cover
        File video_cover_file = extractFirstFrameAsCover(file);
        String coverFileName = "cover_" + fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";
        String video_cover = aliyunOSSOperator.upload(new FileInputStream(video_cover_file), coverFileName);
        log.info("封面url:" + video_cover);

        Original_video originalVideo = new Original_video();
        originalVideo.setVideo_name(fileName);
        originalVideo.setVideo_type(video_type);
        originalVideo.setDuration(duration);
        originalVideo.setVideo_size(formatSize(file.getSize()));
        originalVideo.setVideo_url(video_url);
        originalVideo.setVideo_cover(video_cover);
        originalVideo.setUpload_time(java.time.LocalDateTime.now());
        uploadMapper.insert(originalVideo);

        return video_url;
    }

    /**
     * 格式化文件大小（B/KB/MB/GB）
     */
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 提取视频第一帧作为封面
     * @param videoFile 视频文件（MultipartFile类型）
     * @return 封面图片文件（File类型）
     */
    private File extractFirstFrameAsCover(MultipartFile videoFile) {
        FFmpegFrameGrabber grabber = null;
        File coverFile = null;

        try {
            // 创建临时文件用于处理视频
            File tempVideoFile = File.createTempFile("video_", "_" + videoFile.getOriginalFilename());
            videoFile.transferTo(tempVideoFile);

            // 初始化FFmpegFrameGrabber
            grabber = new FFmpegFrameGrabber(tempVideoFile);
            grabber.start();

            // 跳到第一帧（0秒位置）
            grabber.setTimestamp(0);

            // 抓取视频帧（只抓图像，不抓音频）
            Frame frame = grabber.grabImage();

            if (frame != null) {
                // 将Frame转换为BufferedImage
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.getBufferedImage(frame);

                // 创建临时封面文件
                String coverFileName = UUID.randomUUID().toString() + "_cover.jpg";
                coverFile = File.createTempFile("cover_", ".jpg");

                // 写入图片文件
                ImageIO.write(bufferedImage, "jpg", coverFile);

                // 清理转换器资源
                converter.close();
            }

            // 停止并释放grabber
            grabber.stop();
            grabber.release();

            // 删除临时视频文件
            tempVideoFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
            // 如果发生异常，返回null或根据业务需求处理
            return null;
        } finally {
            // 确保资源被释放
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (FFmpegFrameGrabber.Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return coverFile;
    }
}
