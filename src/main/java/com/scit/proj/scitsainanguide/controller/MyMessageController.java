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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping
    public String selectMyMessageList(Model model, @RequestParam String memberId, @ModelAttribute SearchRequestDTO dto) {
        // pageSize 세팅
        dto.setPageSize(pageSize);

        Page<MessageDTO> messageList = myMessageService.selectMyMessageList(dto, memberId);
        model.addAttribute("messageList", messageList);
        model.addAttribute("page", dto.getPage());
        model.addAttribute("linkSize", linkSize);
        model.addAttribute("filter", dto.getFilter());
        model.addAttribute("filterWord", dto.getFilterWord());
        model.addAttribute("searchType", dto.getSearchType());
        model.addAttribute("searchWord", dto.getSearchWord());
        return "myPage/myMessage";
    }
}
