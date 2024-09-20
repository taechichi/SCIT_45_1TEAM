package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.RealtimeCommentDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.QRealtimeCommentEntity;
import com.scit.proj.scitsainanguide.domain.entity.RealtimeCommentEntity;
import com.scit.proj.scitsainanguide.repository.RealtimeCommentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@Transactional
public class RealtimeCommentRepositoryImpl implements RealtimeCommentRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final JPAQueryFactory queryFactory;
    private QRealtimeCommentEntity realtimeCommentEntity = QRealtimeCommentEntity.realtimeCommentEntity;


    // === CONSTRUCTOR ===
    @Autowired
    public RealtimeCommentRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<RealtimeCommentDTO> selectAllPaging(SearchRequestDTO dto) {
        log.debug("==== 1 ==== selectAllPaging 호출");
        // PageRequest 객체를 생성하여 페이지 번호와 페이지 크기 설정
        Pageable pageable = PageRequest.of(dto.getPage()-1, dto.getPageSize());

        // QueryDSL의 동적 쿼리 생성을 위한 조건 빌더
        // 아직은 필요없음

        // 리스트업 올
        List<RealtimeCommentEntity> realtimeCommentEntityList = queryFactory.selectFrom(realtimeCommentEntity)
                .orderBy(realtimeCommentEntity.createDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        log.debug("==== 2 ==== realtimeCommentEntityList: {}", realtimeCommentEntityList);

        // 전체 카운트 쿼리 ( 페이지을 위한 total count) -> 길이가 나와야 인수로 던져줄 수 있기 때문
        long total = queryFactory.selectFrom(realtimeCommentEntity)
                .fetchCount();
        log.debug("==== 3 ==== total: {}", total);

        // Entity -> DTO 변환 및 Page 변환
        List<RealtimeCommentDTO> tempRealtimeCommentDTOList = realtimeCommentEntityList.stream()
                .map(this::convertToRealtimeCommentDTO)
                .toList();
        log.debug("==== 4 ==== tempRealtimeCommentDTOList: {}", tempRealtimeCommentDTOList);

        return new PageImpl<>(tempRealtimeCommentDTOList, pageable, total);
    }

    // ========== INNER FUNCTION ==========
    private RealtimeCommentDTO convertToRealtimeCommentDTO(RealtimeCommentEntity realtimeCommentEntity) {
        return RealtimeCommentDTO.builder()
                .commentNum(realtimeCommentEntity.getCommentNum())
                .replyNum(realtimeCommentEntity.getReplyRealtimeComment() != null ? realtimeCommentEntity.getReplyRealtimeComment().getCommentNum() : null)
                .location(realtimeCommentEntity.getLocation())
                .contents(realtimeCommentEntity.getContents())
                .createDt(realtimeCommentEntity.getCreateDt())
                .build();
    }
    // ====================================
}
