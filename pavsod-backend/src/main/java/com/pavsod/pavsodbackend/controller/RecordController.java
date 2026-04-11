package com.pavsod.pavsodbackend.controller;

import com.pavsod.pavsodbackend.dto.GetRecordDTO;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.pojo.RecordInfo;
import com.pavsod.pavsodbackend.service.RecordService;
import common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    RecordService recordService;

    @PostMapping("/data")
    public Result GetRecordData(@RequestBody @Validated GetRecordDTO dto){
        try {
            log.info("接收到用户{}的分页记录查询请求", dto.getUserId());
            List<RecordInfo> record_list = recordService.getRecordPage(dto);
            return Result.success(record_list);
        }
        catch (Exception e){
            return Result.error();
        }
    }
}
