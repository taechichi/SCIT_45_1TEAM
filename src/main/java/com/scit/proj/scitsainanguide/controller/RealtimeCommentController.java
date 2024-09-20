package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.RealtimeCommentDTO;
import com.scit.proj.scitsainanguide.service.RealtimeCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RealtimeCommentController {

    private final RealtimeCommentService realtimeCommentService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // SSE 연결을 통해 메시지를 실시간으로 클라이언트에 전송
    @GetMapping(value = "/comments/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamComments() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    // 메시지를 저장하고 모든 클라이언트에게 메시지 전송
    @PostMapping("/comments")
    public void postComments(
            @RequestBody RealtimeCommentDTO comment
    ) throws IOException {

        realtimeCommentService.saveRealtimeComment(comment);    // DB에 메시지 저장

        List<RealtimeCommentDTO> comments = realtimeCommentService.findAllRealtimeComments();

        // 연결된 모든 클라이언트에게 메시지 전송
        for(SseEmitter emitter : emitters) {
            emitter.send(comments);
        }
    }
}
