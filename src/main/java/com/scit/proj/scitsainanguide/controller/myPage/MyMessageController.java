package com.scit.proj.scitsainanguide.controller.myPage;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.myPage.MyFriendService;
import com.scit.proj.scitsainanguide.service.myPage.MyMessageService;
import com.scit.proj.scitsainanguide.service.sse.SseEmitterService;
import com.scit.proj.scitsainanguide.util.PaginationUtils;
import com.scit.proj.scitsainanguide.util.TopbarUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("my/message")
public class MyMessageController {

    private final MyMessageService myMessageService;
    private final SseEmitterService sseEmitterService;
    private final MyFriendService myFriendService;
    private final PaginationUtils paginationUtils;
    private final TopbarUtils topbarUtils;

    @Value("${board.pageSize}")
    private int pageSize;

    /**
     * 내 쪽지 목록 조회 페이지 이동
     *
     * @param user  로그인한 회원 객체
     * @param dto   검색 객체
     * @return 내 쪽지 목록 페이지 정보와 모델을 담은 ModelAndView 객체
     */
    @GetMapping
    public ModelAndView selectMyMessageList(@AuthenticationPrincipal AuthenticatedUser user, @ModelAttribute SearchRequestDTO dto) {
        // pageSize 세팅
        dto.setPageSize(pageSize);

        Page<MessageDTO> messageList = myMessageService.selectMyMessageList(dto, "tester1");
        // 페이징 관련 값들을 세팅하여 ModelAndView 객체를 생성
        ModelAndView modelAndView = paginationUtils.getPaginationData(messageList, dto);
        modelAndView.addObject("pageData", messageList); // 목록 데이터를 모델에 담는다.
        modelAndView.setViewName("myPage/message/myMessage"); // 뷰 이름 설정
        topbarUtils.setTopbarFragmentData(user, modelAndView);  // topbar 관련 데이터 설정

        return modelAndView;
    }

    /**
     * 내 쪽지 다중 삭제
     *
     * @param user          로그인한 회원 객체
     * @param messageIdList 삭제대상 쪽지 아이디 목록
     */
    @ResponseBody
    @DeleteMapping("list")
    public void deleteMyMessageList(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody List<Integer> messageIdList) {
        myMessageService.deleteMyMessageList(user.getId(), messageIdList);
    }

    /**
     * 내 쪽지 삭제
     *
     * @param user      로그인한 회원 객체
     * @param messageId 삭제대상 쪽지 아이디
     */
    @ResponseBody
    @DeleteMapping("{messageId}")
    public void deleteMyMessage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Integer messageId) {
        myMessageService.deleteMyMessage(user.getId(), messageId);
    }

    /**
     * 쪽지 작성 페이지 이동
     *
     * @return 쪽지 작성 페이지 html
     */
    @GetMapping("write")
    public String insertMyMessage() {
        return "myPage/message/myMessageWriteForm";
    }

    /**
     * 쪽지 작성
     *
     * @param dto 쪽지 정보 객체
     * @return redirect 하여 내 쪽지 목록으로 이동
     */
    @PostMapping
    public String insertMyMessage(@AuthenticationPrincipal AuthenticatedUser user, @ModelAttribute MessageDTO dto) {
        myMessageService.insertMyMessage(dto);

        // 쪽지 작성시 받는 사람에게 SSE 를 통해 알림을 전송한다.
        sseEmitterService.sendMessageReceiveNotification("tester5", dto.getReceiverId());
        return "redirect:/my/message";
    }

    /**
     * 내 쪽지 단건 조회 페이지 이동
     *
     * @param model     모델 객체
     * @param messageId 조회대상 쪽지 아이디
     */
    @GetMapping("{messageId}")
    public String selectMyMessage(Model model, @PathVariable Integer messageId) {
        MessageDTO message = myMessageService.selectMyMessage(messageId);
        model.addAttribute("message", message);
        return "myPage/message/messageDetail";
    }

    /**
     * 쪽지 보내기에서 친구 검색 버튼을 누르면 작은 새 창이 열림
     * @param dto
     * @return html
     */
    @GetMapping("searchFriend")
    public String searchFriend(@ModelAttribute SearchRequestDTO dto) {
        return "myPage/message/searchFriend";
    }

    /**
     * 현재 로그인 중인 사용자가 입력한 값을 포함하는 id를 가진 친구 목록 불러옴
     * @param user 현재 로그인 중인 사용자 정보
     * @param searchWord 사용자가 검색창에 입력한 값
     * @return 즐겨찾기, 검색어 포함되는 친구 아이디 알파벳 순으로 정렬된 리스트
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("friendList")
    public List<MemberDTO> list(@AuthenticationPrincipal AuthenticatedUser user,
                                @RequestParam(name = "searchWord", defaultValue = "") String searchWord) throws Exception {
        // 현재 로그인 중인 사용자의 아이디
        String memberId = user.getUsername();
        // 현재 로그인 중인 사용자의 친구 목록에서, 사용자가 입력한 단어를 포함하는 id를 가진 친구 목록 반환
        List<MemberDTO> list = myFriendService.selectMyFriendIdContainSearchWord(memberId, searchWord);

        return list;
    }

}
