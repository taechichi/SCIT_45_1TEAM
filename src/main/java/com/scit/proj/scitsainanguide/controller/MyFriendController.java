package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.service.MyFriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("my")
@RequiredArgsConstructor
public class MyFriendController {

    private final MyFriendService myFriendService;

    @Value("${board.pageSize}")
    private int pageSize;
    @Value("${board.linkSize}")
    private int linkSize;

    /**
     * 내 친구 목록 페이지 이동
     * @param model 모델 객체
     * @param memberId 로그인한 회원
     * @param page 페이지 수
     * @param searchType 검색 조건
     * @param searchWord 검색어
     * @return 내 친구 목록 페이지 html
     */
    @GetMapping("friend/list")
    public String myFriendPage(Model model
            , @RequestParam String memberId
            , @RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "") String searchType
            , @RequestParam(defaultValue = "") String searchWord) {
        Page<FriendDTO> myFriendList = myFriendService.selectMyFriendList(page, pageSize, searchType, searchWord, memberId);
        model.addAttribute("friendList", myFriendList);
        model.addAttribute("page", page);
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);
        return "myPage/myFriend";
    }
}
