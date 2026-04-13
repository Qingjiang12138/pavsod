package com.pavsod.pavsodbackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.github.pagehelper.autoconfigure.PageHelperProperties;
import com.pavsod.pavsodbackend.dto.DeleteRecordDTO;
import com.pavsod.pavsodbackend.dto.GetRecordDTO;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.mapper.RecordMapper;
import com.pavsod.pavsodbackend.pojo.RecordInfo;
import com.pavsod.pavsodbackend.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordMapper recordMapper;

    @Override
    public List<RecordInfo> getRecordPage(GetRecordDTO dto) {

        //设置分页参数
        PageHelper.startPage(dto.getStart_page(), dto.getPer_page());

        //执行查询
        List<Task> records = recordMapper.list(dto);
        List<RecordInfo> recordInfos = new ArrayList<>();

        //用一个recordInfo对象封装一条记录数据
        for (Task record: records){
            RecordInfo recordInfo = new RecordInfo();
            recordInfo.setVideo_id(record.getOriginal_video_id());
            recordInfo.setVideo_name(recordMapper.selectVideoNameById(record.getOriginal_video_id()).substring(36));
            recordInfo.setVideo_url(recordMapper.selectVideoUrlById(record.getOriginal_video_id()));
            recordInfo.setVideo_cover(recordMapper.selectVideoCoverById(record.getOriginal_video_id()));
            recordInfo.setVideo_status(record.getTask_status());
            recordInfo.setTask_create_at(record.getCreate_at());
            recordInfo.setVideo_duration(recordMapper.selectVideoDurationById(record.getOriginal_video_id()));

            recordInfos.add(recordInfo);
        }

        return recordInfos;
    }

    @Override
    public void deleteRecord(DeleteRecordDTO dto) {
        //删除original_video表的一条数据
        recordMapper.deleteOriginalVideoById(dto.getVideo_id());

        //删除salient_video表的一条数据
        recordMapper.deleteSalientVideoByOriginalVideoId(dto.getVideo_id());

        //删除task表的一条数据
        recordMapper.deleteTaskByUserIdAndOriginalVideoId(dto.getUserId(), dto.getVideo_id());
    }

    @Override
    public Integer getRecordCount(Long userId) {

        return recordMapper.getRecordCountByUserId(userId);
    }
}
