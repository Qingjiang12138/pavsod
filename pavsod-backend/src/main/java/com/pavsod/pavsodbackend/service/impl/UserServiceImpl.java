package com.pavsod.pavsodbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;
import com.pavsod.pavsodbackend.dto.UserRegisterDTO;
import com.pavsod.pavsodbackend.entity.Original_video;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.entity.User;
import com.pavsod.pavsodbackend.mapper.OriginalVideoMapper;
import com.pavsod.pavsodbackend.mapper.UserMapper;
import com.pavsod.pavsodbackend.pojo.LoginInfo;
import com.pavsod.pavsodbackend.pojo.RecordInfo;
import com.pavsod.pavsodbackend.service.UserService;
import com.pavsod.pavsodbackend.utils.JWTUtil;
import com.pavsod.pavsodbackend.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private OriginalVideoMapper originalVideoMapper;

    @Autowired
    private UserMapper userMapper;

    //用户注册
    @Override
    public void userRegister(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setDetection_count(0);
        user.setTotal_2d_videos(0);
        user.setTotal_3d_videos(0);
        user.setCurrent_storage(0L);
        user.setMax_storage(10737418240L);//10G

        String encryptedPassword = MD5Util.encrypt(dto.getPassword());
        user.setPassword(encryptedPassword);

        System.out.println("加密密码：" + encryptedPassword);

        userMapper.insert(user);
    }

    @Override
    public LoginInfo userLogin(UserLoginDTO dto){
        //根据用户名和密码查询员工信息
        String encryptedPassword = MD5Util.encrypt(dto.getPassword());
        dto.setPassword(encryptedPassword);

        User user = userMapper.selectByPhoneAndPassword(dto);

        //判断是否存在这个员工，如果存在，组装登录成功的信息
        if(user != null){
            String token = JWTUtil.getToken(user.getId());

            return new LoginInfo(user.getId(), user.getUsername(),
                    user.getPhone(), user.getAvatar(), token);
        }

        //不存在，返回null
        return null;
    }


    @Override
    public Map<String, Object> getHomeData(Long userId){
        Map<String, Object> data = new HashMap<>();
        User user = userMapper.selectByUserId(userId);
        log.info("user get home data:" + user.getTotal_2d_videos());

        //获取本月的检测数量
        Integer month_task = getCurrentMonthDetectionCount(userId);

        //获取累计检测时间
        Integer total_time = getTotalDetectionDuration(userId);

        //获取过去一周每天的检测数量
        int[] week_count = getLast7DaysTaskCount(userId);

        //获取最近五次检测记录
        List<RecordInfo> last_records = new ArrayList<>();

        List<Task> tasks = userMapper.select5LastRecords(userId);
        for(Task task : tasks){
            Long originalVideoId = task.getOriginal_video_id();
            Original_video video = originalVideoMapper.selectVideoById(originalVideoId);
            RecordInfo record = new RecordInfo();

            record.setVideo_id(video.getVideo_id());
            record.setVideo_cover(video.getVideo_cover());
            record.setVideo_name(video.getVideo_name());
            record.setVideo_url(video.getVideo_url());
            record.setVideo_status(task.getTask_status());
            record.setTask_create_at(task.getCreate_at());

            last_records.add(record);
        }

        data.put("total_task", user.getTotal_2d_videos() + user.getTotal_3d_videos());
        data.put("month_task", month_task);
        data.put("total_time", total_time);
        data.put("week_count", week_count);
        data.put("2d_video_count", user.getTotal_2d_videos());
        data.put("3d_video_count", user.getTotal_3d_videos());
        data.put("last_records", last_records);
        
        return data;
    }

    /**
     * 获取用户当月的累计检测数量（本月1号至今）
     */
    private Integer getCurrentMonthDetectionCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        Integer count = userMapper.countCurrentMonthDetectionCount(userId);
        return count != null ? count : 0;
    }

    /**
     * 获取用户累计检测视频的总时长（单位：秒）
     * 统计该用户所有关联的原始视频时长之和
     */
    private Integer getTotalDetectionDuration(Long userId) {
        if (userId == null) {
            return 0;
        }
        // 查询总时长（秒）
        Integer totalSeconds = userMapper.sumTotalDetectionDuration(userId);
        return totalSeconds != null ? totalSeconds : 0;
    }

    /**
     * 获取过去7天每天的检测数量
     * 返回int[7]数组，索引0是7天前，索引6是今天
     */
    private int[] getLast7DaysTaskCount(Long userId) {
        int[] weekCount = new int[7];
        if (userId == null) {
            return weekCount;
        }

        // 查询数据库获取统计
        List<Map<String, Object>> results = userMapper.selectLast7DaysTaskCount(userId);

        // 获取今天日期，计算7天前到今天每一天的日期字符串
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 初始化日期映射（日期 -> 索引）
        Map<String, Integer> dateIndexMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);  // 从6天前到今天
            dateIndexMap.put(date.format(formatter), i);
        }

        // 填充数据
        for (Map<String, Object> result : results) {
            String date = result.get("date").toString();
            Integer count = ((Number) result.get("count")).intValue();

            Integer index = dateIndexMap.get(date);
            if (index != null) {
                weekCount[index] = count;
            }
        }

        return weekCount;
    }

}
