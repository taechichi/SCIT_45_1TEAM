package com.scit.proj.scitsainanguide.service.sse;

import com.scit.proj.scitsainanguide.domain.dto.AlarmDTO;
import com.scit.proj.scitsainanguide.domain.dto.AlarmResponseDTO;
import com.scit.proj.scitsainanguide.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public AlarmResponseDTO selectAlarmList(String memberId) {
        return alarmRepository.selectAlarmList(memberId);
    }
}
