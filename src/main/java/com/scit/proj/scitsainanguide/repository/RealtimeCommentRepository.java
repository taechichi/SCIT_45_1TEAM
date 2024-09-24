package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.RealtimeCommentDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealtimeCommentRepository {
    Page<RealtimeCommentDTO> selectAllPaging(SearchRequestDTO dto);

    List<RealtimeCommentDTO> selectRealtimeCommentAfterSince(String since);
}
