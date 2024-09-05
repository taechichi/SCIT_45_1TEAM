package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;

public interface BoardRepository {

    Page<MarkerBoardDTO> selectDeletedBoardList(SearchRequestDTO dto);
}
