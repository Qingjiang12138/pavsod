package com.pavsod.pavsodbackend.service;

import com.pavsod.pavsodbackend.dto.DeleteRecordDTO;
import com.pavsod.pavsodbackend.dto.GetRecordDTO;
import com.pavsod.pavsodbackend.entity.Task;
import com.pavsod.pavsodbackend.pojo.RecordInfo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


public interface RecordService {

    List<RecordInfo> getRecordPage(GetRecordDTO dto);

    void deleteRecord(DeleteRecordDTO dto);

    Integer getRecordCount(Long userId);
}
