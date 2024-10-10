package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("write")
    public String write(
            @ModelAttribute MarkerBoardDTO boardDTO,
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam("files") MultipartFile[] files
    ) throws IOException {
        // 사용자 아이디 추가
        boardDTO.setMemberId(user.getUsername());
        log.debug("저장할 글 정보 : {}", boardDTO);

        // 파일 개수 및 크기 제한
        int maxFileCount = 4;
        long maxFileSize = 10 * 1024 * 1024;  // 10MB

        // 파일 개수 검사
        if (files.length > maxFileCount) {
            // 파일 개수 초과 시 처리 (다시 작성 페이지로 리다이렉트)
            log.warn("파일 개수 초과: {}개 파일을 업로드하려고 했습니다.", files.length);
            return "redirect:/"; // 에러 메시지와 함께 리다이렉트
        }

        // 파일 크기 검사
        for (MultipartFile file : files) {
            if (file.getSize() > maxFileSize) {
                // 파일 크기 초과 시 처리
                log.warn("파일 크기 초과: {} 파일의 크기가 10MB를 초과했습니다.", file.getOriginalFilename());
                return "redirect:/"; // 에러 메시지와 함께 리다이렉트
            }
        }

        try {
            // 파일 및 게시글 저장 로직
            boardService.write(boardDTO, files);
        } catch (Exception e) {
            log.error("파일 저장 중 오류 발생", e);
            return "redirect:/"; // 저장 실패 시 에러 처리
        }
        boardService.write(boardDTO, files);
        return "redirect:/"; // 성공적으로 저장되었을 때 리다이렉트
    }


    // 게시글 삭제(deleteYn 업데이트)
    @PostMapping("delete/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Integer boardId,
                                              @AuthenticationPrincipal AuthenticatedUser user) {
        // 게시글 정보를 가져옴
        MarkerBoardDTO boardDTO = boardService.findById(boardId);
        String loggedInUserId = user.getUsername();  // getUsername()으로 로그인한 사용자 ID를 가져옴


//        if(boardDTO.getMemberId().equals(loggedInUserId)) {
            boardDTO.setDeleteYn(true);  // deleteYn을 true로 설정하여 논리적으로 삭제 처리
            boardService.updateBoard(boardDTO);  // 수정된 게시글 정보 저장
//        }


        return ResponseEntity.ok().build();
    }

    // 게시글 수정
    @GetMapping("/update/{boardId}")
    public ResponseEntity<MarkerBoardDTO> updateBoard(@PathVariable Integer boardId) {
        MarkerBoardDTO boardDTO = boardService.findById(boardId);
        return ResponseEntity.ok(boardDTO);  // 게시글 데이터를 JSON으로 반환
    }



}
