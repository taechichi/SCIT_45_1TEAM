package com.scit.proj.scitsainanguide.service.sse;

import com.scit.proj.scitsainanguide.domain.enums.AlarmCategory;
import com.scit.proj.scitsainanguide.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final AlarmRepository alarmRepository;

    private final Map<String, SseEmitter> emitters = new HashMap<>();

    // 사용자가 알림을 받을 수 있도록 실시간으로 서버에 연결을 설정
    public SseEmitter createEmitter(String memberId) {
        // 지속적인 연결유지를 위해 Timeout 을 최대한으로 설정 (사실상 무한대의 시간으로 연결을 유지)
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        // memberId 를 key 값으로 하여 Map 에 emitter 저장
        emitters.put(memberId, emitter);

        // 작업이 정상적으로 완료되거나, 타임아웃이 되면 해당 memberId 를 key 값으로 하는 emitter 제거
        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    public void sendFriendRequestNotification(String memberId, String friendIds) {
        // 콤마로 구분된 friendIds를 분리하여 배열로 변환
        for (String friendId : friendIds.split(",")) {
            // 공백 제거
            friendId = friendId.trim();
            String message = memberId + " 님으로부터 친구요청이 도착했습니다."; // 'memberId 님으로부터 친구요청이 도착했습니다.'
            executeAlarm(friendId, message, "friendRequestReceive", AlarmCategory.FRIEND_REQUEST_RECEIVE.getValue());
        }
    }

    public void sendFriendAcceptNotification(String memberId) {
        String message = memberId + " 님이 친구요청을 수락했습니다.";
        executeAlarm(memberId, message, "friendRequestAccept", AlarmCategory.FRIEND_REQUEST_ACCEPT.getValue());
    }

    public void sendMessageReceiveNotification(String senderId, String receiverId) {
        String message = senderId + " 님으로부터 쪽지가 도착했습니다."; // 'senderId 님으로부터 쪽지가 도착했습니다.'
        executeAlarm(receiverId, message, "messageReceive", AlarmCategory.MESSAGE_RECEIVE.getValue());
    }

    private void executeAlarm(String memberId, String message, String eventName, Integer categoryId) {
        SseEmitter emitter = emitters.get(memberId);

        if (emitter != null) {
            try {
                // alarm 테이블에 저장
                alarmRepository.insertAlarm(memberId, message, categoryId);

                // 친구 요청 알림 이벤트 전송 (화면 단에 categoryId 를 전송한다.)
                emitter.send(SseEmitter.event().name(eventName).data(categoryId));
            } catch (IOException e) {
                // 에러 발생 시, Emitter를 오류로 완료 처리
                emitter.completeWithError(e);
            }
        }
    }
}
