package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Value("${board.pageSize}")
    private int pageSize;
    @Value("${board.linkSize}")
    private int linkSize;

    /**
     * 회원 목록 페이지 이동
     * @param model 모델 객체
     * @param page 페이지 수
     * @param filter 목록 필터
     * @param filterWord 필터 내용
     * @param searchType 검색 조건
     * @param searchWord 검색어
     * @return 회원 목록 페이지 html
     */
    @GetMapping("member")
    public String selectMemberList(Model model
            , @RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "") String filter
            , @RequestParam(defaultValue = "") String filterWord
            , @RequestParam(defaultValue = "") String searchType
            , @RequestParam(defaultValue = "") String searchWord) {
        Page<MemberDTO> memberList =
                adminService.selectMemberList(page, pageSize, filter, filterWord, searchType, searchWord);
        model.addAttribute("memberList", memberList);
        model.addAttribute("page", page);
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("filter", filter);
        model.addAttribute("filterWord", filterWord);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);

        return "admin/memberList";
    }

    /**
     * 회원 수정 (관리자 여부만)
     * @param memberId 수정하려는 회원의 아이디
     */
    @ResponseBody
    @PatchMapping("member/{memberId}")
    public void updateMember(@PathVariable("memberId") String memberId) {
        adminService.updateMember(memberId);
    }
}
