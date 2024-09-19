package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.AlarmRequestDTO;
import com.scit.proj.scitsainanguide.domain.dto.AlarmResponseDTO;

public interface AlarmRepository {

    void insertAlarm(String friendId, String message, Integer categoryId);

    AlarmResponseDTO selectAlarmList(String memberId);
}
