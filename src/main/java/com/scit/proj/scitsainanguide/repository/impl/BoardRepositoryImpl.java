package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.*;
import com.scit.proj.scitsainanguide.domain.enums.BoardSearchType;
import com.scit.proj.scitsainanguide.repository.BoardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class BoardRepositoryImpl implements BoardRepository {

    @PersistenceContext
    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private QMarkerBoardEntity markerBoard = QMarkerBoardEntity.markerBoardEntity;

    @Autowired
    public BoardRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Page<MarkerBoardDTO> selectDeletedBoardList(SearchRequestDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        BoardSearchType searchType = BoardSearchType.fromValue(dto.getSearchType());

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(markerBoard.deleteYn.eq(true));

        // 동적 조건 추가
        // 1. 검색조건
        switch (searchType) {
            case MEMBER_ID -> whereClause.and(markerBoard.memberId.contains(dto.getSearchWord()));
            case DELETE_REASON -> whereClause.and(markerBoard.deleteReason.contains(dto.getSearchWord()));
        }

        // 쿼리 실행
        List<MarkerBoardEntity> markerBoardEntityList = queryFactory.selectFrom(markerBoard)
                .where(whereClause)
                .orderBy(markerBoard.deleteDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 (페이징을 위한 total count)
        long total = queryFactory.selectFrom(markerBoard)
                .where(whereClause)
                .fetchCount();

        // Entity -> DTO 변환 및 Page 반환
        List<MarkerBoardDTO> markerBoardDTOList = markerBoardEntityList.stream()
                .map(this::convertToMarkerBoardDTO)
                .toList();

        return new PageImpl<>(markerBoardDTOList, pageable, total);
    }

    @Override
    public Optional<MarkerBoardDTO> selectDeletedBoard(Integer boardId) {
        MarkerBoardEntity markerBoardEntity = queryFactory.selectFrom(markerBoard)
                .from(markerBoard)
                .where(markerBoard.boardId.eq(boardId))
                .fetchOne();

        if (markerBoardEntity == null) {
            throw new EntityNotFoundException("해당하는 게시글을 찾을 수 없습니다.");
        }

        return Optional.ofNullable(convertToMarkerBoardDTO(markerBoardEntity));
    }

    @Override
    public void updateDeletedBoard(Integer boardId) {
        // 해당 게시글의 삭제여부를 다시 false 로 변경
        queryFactory.update(markerBoard)
                .set(markerBoard.deleteYn, false)
                .where(markerBoard.boardId.eq(boardId))
                .execute();
    }

    private MarkerBoardDTO convertToMarkerBoardDTO(MarkerBoardEntity markerBoardEntity) {
        HospitalEntity hospital = markerBoardEntity.getHospital();
        ShelterEntity shelter = markerBoardEntity.getShelter();

        return MarkerBoardDTO.builder()
                .boardId(markerBoardEntity.getBoardId())
                .memberId(markerBoardEntity.getMemberId())
                .hospitalId(hospital != null ? hospital.getHospitalId() : null)
                .shelterId(shelter != null ? shelter.getShelterId() : null)
                .contents(markerBoardEntity.getContents())
                .createDt(markerBoardEntity.getCreateDt())
                .deleteReason(markerBoardEntity.getDeleteReason())
                .deleteYn(markerBoardEntity.getDeleteYn())
                .deleteDt(markerBoardEntity.getDeleteDt())
                .build();
    }
}
