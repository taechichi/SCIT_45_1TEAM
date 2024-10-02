package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.CommentResponseDTO;
import com.scit.proj.scitsainanguide.domain.dto.RealtimeCommentDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.RealtimeCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

        // SSE 연결 관리 로직
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("SSE 연결이 완료되었습니다.");
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.warn("SSE 연결이 시간 초과로 종료되었습니다.");
        });
        emitter.onError((e) -> {
            emitters.remove(emitter);
            log.error("SSE 연결에서 오류가 발생했습니다.", e);
        });

        // 접속한 시점 이후의 댓글만 가져옴
        List<RealtimeCommentDTO> recentComments = realtimeCommentService.findRealtimeCommentsAfterSince(since);

        try {
            // 댓글 데이터를 전송
            emitter.send(recentComments);
        } catch (IOException e) {
            emitters.remove(emitter);
            log.error("SSE 데이터를 전송하는 중 오류 발생", e);
        }

        return emitter;
    }

    // 메시지를 저장하고 모든 클라이언트에게 메시지 전송
    @PostMapping("/comments")
    public ResponseEntity<String> postComments(
            @RequestBody RealtimeCommentDTO comment,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        // 로그인한 사용자만 댓글 작성 가능
        if (user == null) {
            log.debug("로그인되지 않은 사용자의 요청");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 댓글을 작성할 수 있습니다.");
        }

        comment.setCreateDt(LocalDateTime.now(ZoneOffset.UTC));
        comment.setNickname(user.getNickname());
        realtimeCommentService.saveRealtimeComment(comment); // DB에 메시지 저장
        List<RealtimeCommentDTO> comments = List.of(comment);

        // 연결된 모든 클라이언트에게 메시지 전송
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(comments);
            } catch (IOException e) {
                emitters.remove(emitter);
                log.error("SSE 데이터를 클라이언트에게 전송하는 중 오류 발생", e);
            }
        }

        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }
}
