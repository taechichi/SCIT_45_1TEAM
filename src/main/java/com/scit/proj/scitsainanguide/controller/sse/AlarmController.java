package com.scit.proj.scitsainanguide.controller.sse;

import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequiredArgsConstructor
@RequestMapping("notification")
public class AlarmController {

    private final SseEmitterService sseEmitterService;

    /**
     * 사용자가 알림을 받을 수 있도록 실시간으로 서버에 연결
     * @param user 로그인한 유저 객체
     */
    @GetMapping
    public SseEmitter subscribeToNotification(@AuthenticationPrincipal AuthenticatedUser user) {
        return sseEmitterService.createEmitter(user.getId());
    }
}
