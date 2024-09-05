package com.scit.proj.scitsainanguide.service.admin;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminBoardService {

    private final BoardRepository boardRepository;

    public Page<MarkerBoardDTO> selectDeletedBoardList(SearchRequestDTO dto) {
        return boardRepository.selectDeletedBoardList(dto);
    }

    public MarkerBoardDTO selectDeletedBoard(Integer boardId) {
        return boardRepository.selectDeletedBoard(boardId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글을 찾을 수 없습니다."));
    }
}
