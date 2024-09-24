package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.entity.MarkerBoardEntity;
import com.scit.proj.scitsainanguide.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("board")
public class RestBoardController {

    private final BoardService boardService;

    @GetMapping("/list/{placeId}")
    public List<MarkerBoardDTO> findByPlaceId(@PathVariable String placeId) {
        return boardService.findByPlaceId(placeId);
    }
}
