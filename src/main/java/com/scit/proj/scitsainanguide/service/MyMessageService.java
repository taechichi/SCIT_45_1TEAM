package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.repository.MyMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMessageService {

    private final MyMessageRepository myMessageRepository;

    public Page<MessageDTO> selectMyMessageList(SearchRequestDTO dto, String memberId) {
        return myMessageRepository.selectMyMessageList(dto, memberId);
    }

    public void deleteMyMessageList(String memberId, List<Integer> messageIdList) {
        myMessageRepository.deleteMyMessage(memberId, messageIdList);
    }

    public void deleteMyMessage(String memberId, Integer messageId) {
        myMessageRepository.deleteMyMessage(memberId, messageId);
    }

    public void insertMyMessage(MessageDTO dto) {
        myMessageRepository.insertMyMessage(dto);
    }
}
