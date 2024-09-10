package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    @GetMapping("write/{placeID}")
    public String write(@PathVariable("placeID") String placeID, Model model) {

        model.addAttribute("placeID", placeID);

        return "board/writeForm";
    }

    @PostMapping("write")
    public String write(
            @ModelAttribute MarkerBoardDTO boardDTO
            , @AuthenticationPrincipal AuthenticatedUser user
            ) {

        //작성한 글에 사용자 아이디 추가
        boardDTO.setMemberId(user.getUsername());
        log.debug("저장할 글 정보 : {}", boardDTO);

        try {
            boardService.write(boardDTO);
            return "index";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "board/writeForm";
        }
    }

}
