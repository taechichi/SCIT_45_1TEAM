package com.scit.proj.scitsainanguide.controller.admin;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.service.admin.AdminBoardService;
import com.scit.proj.scitsainanguide.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

    private final AdminBoardService adminBoardService;
    private final PaginationUtils paginationUtils;

    @Value("${board.pageSize}")
    private int pageSize;

    /**
     * 관리자 > 삭제된 게시글 목록 조회 페이지 이동
     * @param dto 게시글 정보 객체
     * @return 삭제된 게시글 목록 페이지 정보와 모델을 담은 ModelAndView 객체
     */
    @GetMapping
    public ModelAndView selectDeletedBoardList(@ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);

        Page<MarkerBoardDTO> deletedBoardList = adminBoardService.selectDeletedBoardList(dto);
        // 페이징 관련 값들을 세팅하여 ModelAndView 객체를 생성
        ModelAndView modelAndView = paginationUtils.getPaginationData(deletedBoardList, dto);
        modelAndView.addObject("pageData", deletedBoardList); // 목록 데이터를 모델에 담는다.
        modelAndView.setViewName("admin/board/boardList"); // 뷰 이름 설정

        return modelAndView;
    }

    /**
     * 관리자 > 삭제된 게시글 상세 조회(단건 조회)
     * @param boardId 게시글 아이디
     * @param model 모델 객체
     * @return 삭제된 게시글 상세 조회 페이지 html
     */
    @GetMapping("{boardId}")
    public String selectBoard(@PathVariable Integer boardId, Model model) {
        MarkerBoardDTO deletedBaord = adminBoardService.selectDeletedBoard(boardId);
        model.addAttribute("deletedBoard", deletedBaord);
        return "admin/board/boardDetail";
    }

    /**
     * 관리자 > 삭제된 게시글 업데이트 (현재는 삭제 여부 수정만 가능)
     * @param boardId 게시글 아이디
     */
    @ResponseBody
    @PatchMapping("{boardId}")
    public void updateDeletedBoard(@PathVariable Integer boardId) {
        adminBoardService.updateDeletedBoard(boardId);
    }
}
