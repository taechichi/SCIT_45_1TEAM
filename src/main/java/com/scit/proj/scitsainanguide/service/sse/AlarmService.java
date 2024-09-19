package com.scit.proj.scitsainanguide.service.sse;

import com.scit.proj.scitsainanguide.domain.dto.AlarmRequestDTO;
import com.scit.proj.scitsainanguide.domain.dto.AlarmResponseDTO;
import com.scit.proj.scitsainanguide.repository.AlarmRepository;
import com.scit.proj.scitsainanguide.repository.MyMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final MyMessageRepository myMessageRepository;

    public AlarmResponseDTO selectAlarmList(AlarmRequestDTO alarmRequestDTO) {
        String memberId = alarmRequestDTO.getMemberId();
        AlarmResponseDTO alarmResponseDTO = alarmRepository.selectAlarmList(memberId);

        log.debug("TEST >>>> {}", alarmRequestDTO.getEventType());
        // 새로운 매세지가 온 경우엔 알림뿐만 아니라 읽지않은 메세지 목록도 최신화해줘야한다.
        if ("messageReceive".equals(alarmRequestDTO.getEventType())) {
            alarmResponseDTO.setMessageList(myMessageRepository.selectMyUnreadMessageList(memberId));
            alarmResponseDTO.setMessageCnt(myMessageRepository.selectMyUnreadMessageCnt(memberId));
        }
        return alarmResponseDTO;
    }
}
