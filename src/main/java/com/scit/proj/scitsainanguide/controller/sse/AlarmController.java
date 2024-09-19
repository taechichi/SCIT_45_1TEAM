package com.scit.proj.scitsainanguide.controller.sse;

import com.scit.proj.scitsainanguide.domain.dto.AlarmDTO;
import com.scit.proj.scitsainanguide.domain.dto.AlarmRequestDTO;
import com.scit.proj.scitsainanguide.domain.dto.AlarmResponseDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.sse.AlarmService;
import com.scit.proj.scitsainanguide.service.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    public AlarmResponseDTO selectAlarmList(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam String eventType) {
        log.debug("TEST >>>> {}", eventType);
        return alarmService.selectAlarmList(new AlarmRequestDTO(user.getId(), eventType));
    }
}
