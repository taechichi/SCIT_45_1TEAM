package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface BoardRepository {

    Page<MarkerBoardDTO> selectDeletedBoardList(SearchRequestDTO dto);

    Optional<MarkerBoardDTO> selectDeletedBoard(Integer boardId);

    void updateDeletedBoard(Integer boardId);
}
