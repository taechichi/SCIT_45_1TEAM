package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.RealtimeCommentDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.RealtimeCommentEntity;
import com.scit.proj.scitsainanguide.repository.RealtimeCommentJpaRepository;
import com.scit.proj.scitsainanguide.repository.RealtimeCommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RealtimeCommentService {

    private final RealtimeCommentRepository realtimeCommentRepository;
    private final RealtimeCommentJpaRepository realtimeCommentJpaRepository;

    // =============================================================================
    public Page<RealtimeCommentDTO> updateRealtimeCommentsDSL(SearchRequestDTO dto) {
        return realtimeCommentRepository.selectAllPaging(dto);
    }
    // =============================================================================

    public void saveRealtimeComment(RealtimeCommentDTO dto) {
        // id는 자동생성
        // replynum은 미구현 있으면 있는대로 들어가고 없으면 없는대로 들어가게 해야되는데...
        RealtimeCommentEntity entity = RealtimeCommentEntity.builder()
                .nickname(dto.getNickname())
                .location(dto.getLocation())
                .contents(dto.getContents())
                .build();
        realtimeCommentJpaRepository.save(entity);
    }

    public List<RealtimeCommentDTO> findRealtimeCommentsAfterSince(String since) {
        return realtimeCommentRepository.selectRealtimeCommentAfterSince(since);
    }

    public List<RealtimeCommentDTO> findAllRealtimeComments() {
        List<RealtimeCommentEntity> realtimeCommentEntityList = realtimeCommentJpaRepository.findAll();

        return realtimeCommentEntityList.stream()
                .map(this::convertToRealtimeCommentDTO)
                .toList();
    }

    // ========== INNER FUNCTION ==========
    private RealtimeCommentDTO convertToRealtimeCommentDTO(RealtimeCommentEntity realtimeCommentEntity) {
        return RealtimeCommentDTO.builder()
                .commentNum(realtimeCommentEntity.getCommentNum())
                .nickname(realtimeCommentEntity.getNickname())
                .location(realtimeCommentEntity.getLocation())
                .contents(realtimeCommentEntity.getContents())
                .createDt(realtimeCommentEntity.getCreateDt())
                .build();
    }
    // ====================================
}
