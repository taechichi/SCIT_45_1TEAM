package com.scit.proj.scitsainanguide.controller.sse;

import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmRequestDTO;
import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmResponseDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.sse.AlarmService;
import com.scit.proj.scitsainanguide.service.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("notification")
public class AlarmController {

    private final SseEmitterService sseEmitterService;
    private final AlarmService alarmService;

    /**
     * 사용자가 알림을 받을 수 있도록 실시간으로 서버에 연결
     * @param user 로그인한 유저 객체
     */
    @GetMapping
    public SseEmitter subscribeToNotification(@AuthenticationPrincipal AuthenticatedUser user) {
        return sseEmitterService.createEmitter(user.getId());
    }

    /**
     * 사용자의 알림 목록 조회
     * @param user 로그인한 유저 객체
     * @return 알림 정보 목록 객체
     */
    @GetMapping("list")
    public SelectAlarmResponseDTO selectAlarmList(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam String eventType) {
        return alarmService.selectAlarmList(new SelectAlarmRequestDTO(user.getId(), eventType));
    }

    /**
     * 알림 읽음 처리
     * @param user 로그인한 유저 정보 객체
     * @param alarmIdList 읽음 처리할 알림 번호 목록
     */
    @PatchMapping("list")
    public void updateAlarmReadYn(@AuthenticationPrincipal AuthenticatedUser user, List<Integer> alarmIdList) {
        alarmService.updateAlarmReadYn(user.getId(), alarmIdList);
    }
}
