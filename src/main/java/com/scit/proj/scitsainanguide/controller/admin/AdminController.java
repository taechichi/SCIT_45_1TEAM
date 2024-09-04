package com.scit.proj.scitsainanguide.controller.admin;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.service.admin.AdminService;
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
     * @param dto 검색 요청 객체
     * @return 회원 목록 페이지 html
     */
    @GetMapping("member")
    public String selectMemberList(Model model, @ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);
        
        Page<MemberDTO> memberList =
                adminService.selectMemberList(dto);
        model.addAttribute("memberList", memberList);
        model.addAttribute("page", dto.getPage());
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("filter", dto.getFilter());
        model.addAttribute("filterWord", dto.getFilterWord());
        model.addAttribute("searchType", dto.getSearchType());
        model.addAttribute("searchWord", dto.getSearchWord());

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
