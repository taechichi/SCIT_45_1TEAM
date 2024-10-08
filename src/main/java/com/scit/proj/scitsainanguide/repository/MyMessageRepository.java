package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.MessageDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MyMessageRepository {

    Page<MessageDTO> selectMyMessageList(SearchRequestDTO dto, String memberId);

    void deleteMyMessage(String memberId, List<Integer> messageIdList);

    void deleteMyMessage(String memberId, Integer messageId);

    void insertMyMessage(MessageDTO dto);

    Optional<MessageDTO> selectMyMessage(Integer messageId);

    List<MessageDTO> selectMyUnreadMessageList(String memberId);

    void updateMessageReadDt(Integer messageId);

    Long selectMyUnreadMessageCnt(String memberId);
}
