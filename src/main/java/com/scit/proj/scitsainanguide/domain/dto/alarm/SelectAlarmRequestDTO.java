package com.scit.proj.scitsainanguide.domain.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectAlarmRequestDTO {

    private String memberId;
    private String eventType;

}
