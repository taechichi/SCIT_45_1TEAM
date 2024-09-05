package com.scit.proj.scitsainanguide.controller.admin;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.service.admin.AdminBoardService;
import com.scit.proj.scitsainanguide.service.admin.AdminMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

    private final AdminBoardService adminBoardService;

    @Value("${board.pageSize}")
    private int pageSize;
    @Value("${board.linkSize}")
    private int linkSize;

    /**
     * 관리자 > 삭제된 게시글 목록 조회 페이지 이동
     * @param model 모델 객체
     * @param dto 게시글 정보 객체
     * @return 삭제된 게시글 목록 조회 페이지 html
     */
    @GetMapping
    public String selectDeletedBoardList(Model model, @ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);

        Page<MarkerBoardDTO> deletedBoardList = adminBoardService.selectDeletedBoardList(dto);
        model.addAttribute("deletedBoardList", deletedBoardList);
        model.addAttribute("page", dto.getPage());
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("searchType", dto.getSearchType());
        model.addAttribute("searchWord", dto.getSearchWord());
        return "admin/boardList";
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
        return "admin/boardDetail";
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
