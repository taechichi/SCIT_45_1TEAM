package com.scit.proj.scitsainanguide.domain.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDTO {

    private Integer alarmId;
    private Integer categoryId;
    private String memberId;
    private String contents;
    private LocalDateTime alarmDt;
    private Boolean readYn;
}
