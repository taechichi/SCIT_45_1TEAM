package com.scit.proj.scitsainanguide.controller.myPage;

import com.scit.proj.scitsainanguide.domain.dto.FriendDTO;
import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.myPage.MyFriendService;
import com.scit.proj.scitsainanguide.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("my/friend")
@RequiredArgsConstructor
public class MyFriendController {

    private final MyFriendService myFriendService;
    private final PaginationUtils paginationUtils;

    @Value("${board.pageSize}")
    private int pageSize;

    /**
     * 내 친구 목록 페이지 이동
     * @param user 로그인한 회원 객체
     * @param dto 검색 요청 객체
     * @return 내 친구 목록 페이지 정보와 모델을 담은 ModelAndView 객체
     */
    @GetMapping
    public ModelAndView selectMyFriendList(@AuthenticationPrincipal AuthenticatedUser user, @ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);

        Page<FriendDTO> myFriendList = myFriendService.selectMyFriendList(dto, user.getId());
        // 페이징 관련 값들을 세팅하여 ModelAndView 객체를 생성
        ModelAndView modelAndView = paginationUtils.getPaginationData(myFriendList, dto);
        modelAndView.addObject("pageData", myFriendList); // 목록 데이터를 모델에 담는다.
        modelAndView.setViewName("myPage/friend/myFriend"); // 뷰 이름 설정

        return modelAndView;
    }

    /**
     * 내 즐겨찾기한 친구 목록
     * @param user 로그인한 회원 객체
     * @return 친구 정보 객체
     */
    @ResponseBody
    @GetMapping("favorite")
    public List<MemberDTO> selectMyFavoriteList(@AuthenticationPrincipal AuthenticatedUser user) {
        return myFriendService.selectMyFavoriteList(user.getId());
    }

    /**
     * 친구 상세정보 조회
     * @param memberId 조회대상 친구의 아이디
     * @return 친구의 회원 정보 객체
     */
    @ResponseBody
    @GetMapping("{memberId}")
    public MemberDTO selectMyFriend(@PathVariable String memberId) {
        // TODO 국제화 기능 마무리되면 statusName 도 언어 설정에 따라 다르게 조회되어야함
        return myFriendService.selectMyFriend(memberId);
    }

    /**
     * 내 친구 신청 목록 페이지 이동
     * @param user 로그인한 회원 객체
     * @param dto 검색 요청 객체
     * @return 내 친구 신청 목록 페이지 정보와 모델을 담은 ModelAndView 객체
     */
    @GetMapping("request")
    public ModelAndView selectMyFriendRequestList(@AuthenticationPrincipal AuthenticatedUser user, @ModelAttribute SearchRequestDTO dto) {
        // dto 에 pageSize 셋팅
        dto.setPageSize(pageSize);

        Page<FriendDTO> myFriendRequestList = myFriendService.selectMyFriendRequestList(dto, user.getId());
        // 페이징 관련 값들을 세팅하여 ModelAndView 객체를 생성
        ModelAndView modelAndView = paginationUtils.getPaginationData(myFriendRequestList, dto);
        modelAndView.addObject("pageData", myFriendRequestList); // 목록 데이터를 모델에 담는다.
        modelAndView.setViewName("myPage/friend/myFriendRequest"); // 뷰 이름 설정

        return modelAndView;
    }

    /**
     * 친구 관계 추가 (친구 신청시)
     * @param user 로그인한 회원 객체
     * @param friendIds 친구추가 대상회원 아이디를 콤마로 구분해 여러 아이디 한 번에 가져옴
     */
    @ResponseBody
    @PostMapping("{friendIds}")
    public void insertFriend(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable String friendIds) {
        myFriendService.insertFriend(user.getId(), friendIds);
    }

    /**
     * 친구 즐겨찾기
     * @param relationId 친구관계 아이디
     * @param user 로그인 유저 객체
     */
    @ResponseBody
    @PatchMapping("{relationId}")
    public void updateFriend(@PathVariable Integer relationId, @AuthenticationPrincipal AuthenticatedUser user) {
        myFriendService.updateFriend(relationId, user.getId());
    }

    /**
     * 친구 관계 삭제
     * @param user 로그인 유저 객체
     * @param friendId 친구삭제 대상회원
     */
    @ResponseBody
    @DeleteMapping("{friendId}")
    public void deleteFriend(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable String friendId) {
        myFriendService.deleteFriend(user.getId(), friendId);
    }

    /**
     * 친구 신청 수락
     * @param user 로그인 유저 객체
     * @param relationId 친구관계 아이디
     */
    @ResponseBody
    @PostMapping("accept/{relationId}")
    public void acceptFriend(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Integer relationId) {
        myFriendService.acceptFriend(user.getId(), relationId);
    }

    /**
     * 친구 신청 거절
     * @param user 로그인 유저 객체
     * @param relationId 친구관계 아이디
     */
    @ResponseBody
    @PostMapping("reject/{relationId}")
    public void rejectFriend(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Integer relationId) {
        myFriendService.rejectFriend(user.getId(), relationId);
    }

}
