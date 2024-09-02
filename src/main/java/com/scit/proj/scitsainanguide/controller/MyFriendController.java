package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.service.MyFriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("my/friend")
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
     * @param dto 검색 요청 객체
     * @return 내 친구 목록 페이지 html
     */
    @GetMapping
    public String selectMyFriendList(Model model, @RequestParam String memberId, @ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);

        Page<FriendDTO> myFriendList = myFriendService.selectMyFriendList(dto, memberId);
        model.addAttribute("friendList", myFriendList);
        model.addAttribute("page", dto.getPage());
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("searchType", dto.getSearchType());
        model.addAttribute("searchWord", dto.getSearchWord());
        return "myPage/myFriend";
    }

    /**
     * 내 친구 신청 목록 페이지 이동
     * @param model 모델 객체
     * @param memberId 로그인한 회원
     * @param dto 검색 요청 객체
     * @return 내 친구 신청 목록 페이지 html
     */
    @GetMapping("request")
    public String selectMyFriendRequestList(Model model, @RequestParam String memberId, @ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);

        Page<FriendDTO> myFriendRequestList = myFriendService.selectMyFriendRequestList(dto, memberId);
        model.addAttribute("friendRequestList", myFriendRequestList);
        model.addAttribute("page", dto.getPage());
        model.addAttribute("linkSize", linkSize);
        return "myPage/myFriendRequest";
    }

    /**
     * 친구 관계 추가 (친구 신청시)
     * @param memberId 친구추가 신청회원
     * @param friendId 친구추가 대상회원
     */
    @ResponseBody
    @PostMapping
    public void insertFriend(@RequestParam String memberId, @RequestParam String friendId) {
        myFriendService.insertFriend(memberId, friendId);
    }

    /**
     * 친구 즐겨찾기
     * @param relationId 친구관계 아이디
     */
    @ResponseBody
    @PatchMapping("{relationId}")
    public void updateFriend(@PathVariable Integer relationId) {
        myFriendService.updateFriend(relationId);
    }

    /**
     * 친구 관계 삭제
     * @param memberId 친구삭제 신청회원
     * @param friendId 친구삭제 대상회원
     */
    @ResponseBody
    @DeleteMapping
    public void deleteFriend(@RequestParam String memberId, @RequestParam String friendId) {
        myFriendService.deleteFriend(memberId, friendId);
    }

    /**
     * 친구 신청 수락
     * @param memberId 친구신청 수락회원
     * @param relationId 친구관계 아이디
     */
    @ResponseBody
    @PostMapping("accept/{relationId}")
    public void acceptFriend(@RequestParam String memberId, @PathVariable Integer relationId) {
        myFriendService.acceptFriend(memberId, relationId);
    }

    /**
     * 친구 신청 거절
     * @param memberId 친구신청 거절회원
     * @param relationId 친구관계 아이디
     */
    @ResponseBody
    @PostMapping("reject/{relationId}")
    public void rejectFriend(@RequestParam String memberId, @PathVariable Integer relationId) {
        myFriendService.rejectFriend(memberId, relationId);
    }

}
