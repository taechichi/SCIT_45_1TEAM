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
    ) {
        // 사용자 아이디 추가
        boardDTO.setMemberId(user.getUsername());
        log.debug("저장할 글 정보 : {}", boardDTO);

        try {
            boardService.write(boardDTO, files);
            return "redirect:/";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    // 게시글 삭제(deleteYn 업데이트)
    @PostMapping("delete/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Integer boardId,
                                              @AuthenticationPrincipal AuthenticatedUser user) {
        // 게시글 정보를 가져옴
        MarkerBoardDTO boardDTO = boardService.findById(boardId);
        String loggedInUserId = user.getUsername();  // getUsername()으로 로그인한 사용자 ID를 가져옴

        boardDTO.setDeleteYn(true);  // deleteYn을 true로 설정하여 논리적으로 삭제 처리
        boardService.updateBoard(boardDTO);  // 수정된 게시글 정보 저장

        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

}
