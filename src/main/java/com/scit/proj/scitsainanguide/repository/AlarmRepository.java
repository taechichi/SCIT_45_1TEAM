package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmResponseDTO;

import java.util.List;

public interface AlarmRepository {

    void insertAlarm(String friendId, String message, Integer categoryId);

    SelectAlarmResponseDTO selectAlarmList(String memberId);

    void updateAlarmReadYn(String memberId, List<Integer> alarmIdList);
}
