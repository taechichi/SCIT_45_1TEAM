package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.service.MyMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("my/message")
public class MyMessageController {

    private final MyMessageService myMessageService;

    @Value("${board.pageSize}")
    private int pageSize;
    @Value("${board.linkSize}")
    private int linkSize;

    /**
     * 내 쪽지 목록 조회 페이지 이동
     * @param model 모델 객체
     * @param memberId 로그인 한 아이디
     * @param dto 검색 객체
     * @return 내 쪽지 목록 조회 페이지 html
     */
    @GetMapping
    public String selectMyMessageList(Model model, @RequestParam String memberId, @ModelAttribute SearchRequestDTO dto) {
        // pageSize 세팅
        dto.setPageSize(pageSize);

        Page<MessageDTO> messageList = myMessageService.selectMyMessageList(dto, memberId);
        model.addAttribute("messageList", messageList);
        model.addAttribute("page", dto.getPage());
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("searchType", dto.getSearchType());
        model.addAttribute("searchWord", dto.getSearchWord());
        return "myPage/myMessage";
    }

    /**
     * 내 쪽지 다중 삭제
     * @param memberId 로그인 한 아이디
     * @param messageIdList 삭제대상 쪽지 아이디 목록
     */
    @ResponseBody
    @DeleteMapping("list")
    public void deleteMyMessageList(@RequestParam String memberId, @RequestParam List<Integer> messageIdList) {
        myMessageService.deleteMyMessageList(memberId, messageIdList);
    }

    /**
     * 내 쪽지 삭제
     * @param memberId 로그인 한 아이디
     * @param messageId 삭제대상 쪽지 아이디
     */
    @ResponseBody
    @DeleteMapping
    public void deleteMyMessage(@RequestParam String memberId, @RequestParam Integer messageId) {
        myMessageService.deleteMyMessage(memberId, messageId);
    }

    /**
     * 쪽지 작성
     * @param dto 쪽지 정보 객체
     * @return redirect 하여 내 쪽지 목록으로 이동
     */
    @PostMapping
    public String insertMyMessage(@ModelAttribute MessageDTO dto) {
        myMessageService.insertMyMessage(dto);
        return "redirect:/my/message/list";
    }
}
