package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MemberService memberService;

    /**
     * 메인 페이지 이동
     */
    @GetMapping
    public String index(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        MemberDTO memberDTO = null;
        if (user != null) {
            memberDTO = memberService.findByMemberId(user.getUsername());
        } else {
            // user가 null일 때 처리 (예: 로그인하지 않은 상태)
            memberDTO = new MemberDTO();
        }

        model.addAttribute("member", memberDTO);
        return "index";
    }

}
