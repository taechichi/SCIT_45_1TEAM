package com.scit.proj.scitsainanguide.domain.dto.alarm;

import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectAlarmResponseDTO {

    private List<AlarmDTO> alarmList;

    private Long alarmCnt;

    private List<MessageDTO> messageList;

    private Long messageCnt;

    public SelectAlarmResponseDTO(List<AlarmDTO> alarmList, Long alarmCnt) {
        this.alarmList = alarmList;
        this.alarmCnt = alarmCnt;
    }
}
