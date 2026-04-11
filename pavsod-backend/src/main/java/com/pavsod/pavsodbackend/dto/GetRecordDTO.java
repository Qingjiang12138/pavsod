package com.pavsod.pavsodbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GetRecordDTO {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotNull
    private Integer start_page;

    @NotNull
    private Integer per_page;

    private String find_name;
    private LocalDate find_date;
}
