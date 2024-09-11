package com.scit.proj.scitsainanguide.controller.admin;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.service.admin.AdminMemberService;
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
@RequestMapping("admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;
    private final PaginationUtils paginationUtils;

    @Value("${board.pageSize}")
    private int pageSize;

    /**
     * 회원 목록 페이지 이동
     * @param dto 검색 요청 객체
     * @return 회원 목록 페이지 정보와 모델을 담은 ModelAndView 객체
     */
    @GetMapping
    public ModelAndView selectMemberList(@ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);

        Page<MemberDTO> memberList = adminMemberService.selectMemberList(dto);
        // 페이징 관련 값들을 세팅하여 ModelAndView 객체를 생성
        ModelAndView modelAndView = paginationUtils.getPaginationData(memberList, dto);
        modelAndView.addObject("pageData", memberList); // 목록 데이터를 모델에 담는다.
        modelAndView.setViewName("admin/member/memberList"); // 뷰 이름 설정

        return modelAndView;
    }

    /**
     * 회원 수정 (관리자 여부만)
     * @param memberId 수정하려는 회원의 아이디
     */
    @ResponseBody
    @PatchMapping("{memberId}")
    public void updateMember(@PathVariable("memberId") String memberId) {
        adminMemberService.updateMember(memberId);
    }
}
