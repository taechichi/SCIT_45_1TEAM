package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.repository.MyMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMessageService {

    private final MyMessageRepository myMessageRepository;

    public Page<MessageDTO> selectMyMessageList(SearchRequestDTO dto, String memberId) {
        return myMessageRepository.selectMyMessageList(dto, memberId);
    }
}
