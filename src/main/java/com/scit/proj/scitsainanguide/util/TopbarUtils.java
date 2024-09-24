package com.scit.proj.scitsainanguide.util;

import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmRequestDTO;
import com.scit.proj.scitsainanguide.domain.dto.alarm.SelectAlarmResponseDTO;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.MemberService;
import com.scit.proj.scitsainanguide.service.myPage.MyFriendService;
import com.scit.proj.scitsainanguide.service.myPage.MyMessageService;
import com.scit.proj.scitsainanguide.service.sse.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TopbarUtils {

    private final MemberService memberService;
    private final MyMessageService myMessageService;
    private final MyFriendService myFriendService;
    private final AlarmService alarmService;

    // 처음부터 ModelAndView 객체를 만들어서 사용해야할 경우
    public ModelAndView setTopbarFragmentData(AuthenticatedUser user, String viewName) {
        ModelAndView modelAndView = new ModelAndView();
        setTopbarFragmentData(user, modelAndView);
        modelAndView.setViewName(viewName);

        return modelAndView;
    }
    
    // 목록 조회 페이지 같은 경우에는 ModelAndView 객체가 이미 만들어져있기 때문에 해당 객체를 바로 활용하기위해 메서드 오버로딩함
    public ModelAndView setTopbarFragmentData(AuthenticatedUser user, ModelAndView modelAndView) {
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

            // 내가 읽지않은 알람 목록 정보 세팅
            SelectAlarmRequestDTO alarmRequestDTO = new SelectAlarmRequestDTO();
            alarmRequestDTO.setMemberId(memberId);
            SelectAlarmResponseDTO alarmResponseDTO = alarmService.selectAlarmList(alarmRequestDTO);
            modelAndView.addObject("alarmList", alarmResponseDTO.getAlarmList());
            modelAndView.addObject("alarmCnt", alarmResponseDTO.getAlarmCnt());
        }
        modelAndView.addObject("member", memberDTO);
        return modelAndView;
    }
}
