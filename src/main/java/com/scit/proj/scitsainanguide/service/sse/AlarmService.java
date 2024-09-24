package com.scit.proj.scitsainanguide.service.sse;

import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmRequestDTO;
import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmResponseDTO;
import com.scit.proj.scitsainanguide.repository.AlarmRepository;
import com.scit.proj.scitsainanguide.repository.MyMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final MyMessageRepository myMessageRepository;

    public SelectAlarmResponseDTO selectAlarmList(SelectAlarmRequestDTO alarmRequestDTO) {
        String memberId = alarmRequestDTO.getMemberId();
        SelectAlarmResponseDTO alarmResponseDTO = alarmRepository.selectAlarmList(memberId);

        // 새로운 매세지가 온 경우엔 알림뿐만 아니라 읽지않은 메세지 목록도 최신화해줘야한다.
        if ("messageReceive".equals(alarmRequestDTO.getEventType())) {
            alarmResponseDTO.setMessageList(myMessageRepository.selectMyUnreadMessageList(memberId));
            alarmResponseDTO.setMessageCnt(myMessageRepository.selectMyUnreadMessageCnt(memberId));
        }
        return alarmResponseDTO;
    }

    public void updateAlarmReadYn(String memberId, List<Integer> alarmIdList) {
        alarmRepository.updateAlarmReadYn(memberId, alarmIdList);
    }
}
