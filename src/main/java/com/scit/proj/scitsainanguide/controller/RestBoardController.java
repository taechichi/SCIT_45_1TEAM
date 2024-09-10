package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("board")
public class RestBoardController {

    private final BoardService boardService;

    @PostMapping("write")
    public ResponseEntity<String> write(
            @RequestBody MarkerBoardDTO boardDTO,  // JSON으로 받은 데이터를 처리
            @RequestParam("placeId") String placeId,  // JavaScript에서 전송된 placeId
            @AuthenticationPrincipal AuthenticatedUser user
    ) {

        // 사용자 ID 설정
        boardDTO.setMemberId(user.getUsername());

        log.debug("저장할 글 정보: {}", boardDTO);
        log.debug("받은 Place ID: {}", placeId);

        try {
            // 글 작성 서비스 호출
            boardService.write(boardDTO);
            return new ResponseEntity<>("글 작성 성공", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("글 작성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
