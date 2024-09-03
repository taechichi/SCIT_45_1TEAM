package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;

public interface MyMessageRepository {

    Page<MessageDTO> selectMyMessageList(SearchRequestDTO dto, String memberId);
}
