package com.scit.proj.scitsainanguide.service.main;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.MemberService;
import com.scit.proj.scitsainanguide.service.myPage.MyFriendService;
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
    private final MyFriendService myFriendService;

    public ModelAndView selectIndexPageData(AuthenticatedUser user) {
        ModelAndView modelAndView = new ModelAndView();
        MemberDTO memberDTO = new MemberDTO();

        if (user != null) {
            String memberId = user.getUsername();
            // 로그인한 유저 정보 세팅
            memberDTO = memberService.findByMemberId(memberId);

            // 내가 읽지않은 메세지 목록 정보 세팅
            List<MessageDTO> messageDTOList = myMessageService.selectMyUnreadMessageList(memberId);
            Long unreadMessageCnt = myMessageService.selectMyUnreadMessageCnt(memberId);
            modelAndView.addObject("unreadMessageList", messageDTOList);
            modelAndView.addObject("unreadMessageCnt", unreadMessageCnt);

            // 내가 즐겨찾기한 친구 정보 세팅
            List<MemberDTO> friendDTOList = myFriendService.selectMyFavoriteList(memberId);
            modelAndView.addObject("favoriteFriendList", friendDTOList);
        }
        modelAndView.addObject("member", memberDTO);
        modelAndView.setViewName("index");

        return modelAndView;
    }
}
