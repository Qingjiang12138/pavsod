package com.pavsod.pavsodbackend.service.impl;

import com.pavsod.pavsodbackend.entity.Original_video;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.exception.BusinessException;
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
        //用户表对应视频类型检测数量加一
        if(video_type.contains("2"))
            uploadMapper.user2dDetectCountAdd(userId);
        if(video_type.contains("3"))
            uploadMapper.user3dDetectCountAdd(userId);

        //向original_video表插入一条数据
        String fileName = UUID.randomUUID() + file.getOriginalFilename();

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
        originalVideo.setVideo_size(file.getSize());
        originalVideo.setVideo_url(video_url);
        originalVideo.setVideo_cover(video_cover);
        originalVideo.setUpload_time(java.time.LocalDateTime.now());
        uploadMapper.insert(originalVideo);
        log.info("插入一条original_video数据");


        // 在更新前检查是否超限
        User user = uploadMapper.selectUserById(userId);
        long maxStorage = user.getMax_storage();      // 最大空间
        long currentStorage = user.getCurrent_storage(); // 已用空间
        long newSize = file.getSize();

        if (currentStorage + newSize > maxStorage) {
            long remain = maxStorage - currentStorage;
            throw new BusinessException(
                    String.format("存储空间不足，需要%s，剩余%s",
                            formatSize(newSize), formatSize(remain))
            );
        }
        //user表储存空间增加
        long videoSize = file.getSize();
        uploadMapper.userStorageAdd(userId, videoSize);
        log.info("用户{}存储空间增加{}字节", userId, videoSize);

        //向task表插入一条数据
        Long originalVideoId = uploadMapper.selectOriginalVideoIdByName(fileName);
        System.out.println(originalVideoId);
        Task task = new Task();

        task.setUser_id(userId);
        task.setOriginal_video_id(originalVideoId);
        task.setVideo_type(video_type);
        task.setTarget_fps(frame);
        task.setProcess_count(1);
        task.setTask_status(0);
        task.setCreate_at(java.time.LocalDateTime.now());
        uploadMapper.insertTask(task);
        log.info("插入一条task数据");

        return video_url;
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

    // 辅助方法：字节转可读格式
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return String.format("%.2fKB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2fMB", bytes / (1024.0 * 1024));
        return String.format("%.2fGB", bytes / (1024.0 * 1024 * 1024));
    }
}
