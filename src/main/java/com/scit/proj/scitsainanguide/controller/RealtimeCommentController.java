package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.RealtimeCommentDTO;
import com.scit.proj.scitsainanguide.service.RealtimeCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
    public SseEmitter streamComments(
            @RequestParam("since") String since
    ) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        // 접속한 시점 이후의 댓글만 가져옴
        List<RealtimeCommentDTO> recentComments = realtimeCommentService.findRealtimeCommentsAfterSince(since);

        try {
            emitter.send(recentComments);
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    // 메시지를 저장하고 모든 클라이언트에게 메시지 전송
    @PostMapping("/comments")
    public void postComments(
            @RequestBody RealtimeCommentDTO comment
    ) {

        realtimeCommentService.saveRealtimeComment(comment);    // DB에 메시지 저장

        List<RealtimeCommentDTO> comments = List.of(comment);

        // 연결된 모든 클라이언트에게 메시지 전송
        for(SseEmitter emitter : emitters) {
            try {
                emitter.send(comments);
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
