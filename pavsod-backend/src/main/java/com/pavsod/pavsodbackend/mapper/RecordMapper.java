package com.pavsod.pavsodbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pavsod.pavsodbackend.dto.GetRecordDTO;
import com.pavsod.pavsodbackend.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordMapper extends BaseMapper<Task> {

    List<Task> list(@Param("dto") GetRecordDTO dto);

    String selectVideoNameById(Long originalVideoId);

    String selectVideoUrlById(Long originalVideoId);

    String selectVideoCoverById(Long originalVideoId);
}
