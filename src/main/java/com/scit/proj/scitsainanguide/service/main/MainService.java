package com.scit.proj.scitsainanguide.service.main;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.MemberService;
import com.scit.proj.scitsainanguide.service.myPage.MyMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final MemberService memberService;
    private final MyMessageService myMessageService;

    public ModelAndView selectIndexPageData(AuthenticatedUser user) {
        ModelAndView modelAndView = new ModelAndView();
        boolean isLogined = false;

        // 로그인한 유저 정보 세팅
        MemberDTO memberDTO = new MemberDTO();
        if (user != null) {
            memberDTO = memberService.findByMemberId(user.getUsername());
            isLogined = true;
        }
        modelAndView.addObject("member", memberDTO);

        // 내가 읽지않은 메세지 목록 정보 세팅
        if (isLogined) {
            List<MessageDTO> messageDTOList = myMessageService.selectMyUnreadMessageList(user.getUsername());
            modelAndView.addObject("unreadMessageList", messageDTOList);
        }

        modelAndView.setViewName("index");
        return modelAndView;
    }
}
