package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.entity.MarkerBoardEntity;
import com.scit.proj.scitsainanguide.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("board")
public class RestBoardController {

    private final BoardService boardService;

    @GetMapping("/list/{placeID}")
    public List<MarkerBoardDTO> getBoardList(
            @PathVariable String placeID,
            @RequestParam int page,    // 클라이언트에서 요청한 페이지 번호
            @RequestParam int size     // 클라이언트에서 요청한 페이지 크기 (게시글 개수)
    ) {
        // 페이지와 크기를 전달하여 게시글 리스트를 가져옴
        return boardService.findByPlaceIdWithPaging(placeID, page, size);
    }
}
