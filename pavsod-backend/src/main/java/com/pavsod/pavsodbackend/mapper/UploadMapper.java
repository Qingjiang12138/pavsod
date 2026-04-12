package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.entity.Original_video;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UploadMapper extends BaseMapper<Original_video> {

    Long selectOriginalVideoIdByName(String fileName);

    void insertTask(Task task);

    void user2dDetectCountAdd(Long userId);

    void user3dDetectCountAdd(Long userId);

    void userStorageAdd(Long userId, long videoSize);

    User selectUserById(Long userId);
}
