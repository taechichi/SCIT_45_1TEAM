package com.scit.proj.scitsainanguide.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import com.scit.proj.scitsainanguide.domain.entity.QMarkerFavoritesEntity;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@Transactional
public class MarkerFavoritesRepositoryImpl implements MarkerFavoritesRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final JPAQueryFactory queryFactory;
    private QMarkerFavoritesEntity markerFavoritesEntity = QMarkerFavoritesEntity.markerFavoritesEntity;

    // === CONSTRUCTOR ===
    public MarkerFavoritesRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<MarkerFavoritesDTO> selectMarkerFavoritesList(SearchRequestDTO dto, String memberId) {
        // PageRequest 객체를 생성하여 페이지 번호와 페이지 크기 설정
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());

        // QueryDSL의 동적 쿼리 생성을 위한 조건 빌더
        //BooleanBuilder whereClause = new BooleanBuilder();
        // ↓ 이거슨 조건설정하는 거
        //whereClause.and();

        // 리스트업
        List<MarkerFavoritesEntity> markerFavoritesEntityList = queryFactory.selectFrom(markerFavoritesEntity)
                .orderBy(markerFavoritesEntity.favoriteId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 ( 페이지을 위한 total count) -> 길이가 나와야 인수로 던져줄 수 있기 때문
        long total = queryFactory.selectFrom(markerFavoritesEntity)
                .fetchCount();
        
        // Entity -> DTO 변환 및 Page 변환
        List<MarkerFavoritesDTO> tempMarkerFavoritesDTOList = markerFavoritesEntityList.stream()
                .map(this::convertToMarkerFavoritesDTO)
                .toList();

        return new PageImpl<>(tempMarkerFavoritesDTOList, pageable, total);
    }

    private MarkerFavoritesDTO convertToMarkerFavoritesDTO(MarkerFavoritesEntity markerFavoritesEntity) {
        return MarkerFavoritesDTO.builder()
                .favoriteId(markerFavoritesEntity.getFavoriteId())
                .memberId(markerFavoritesEntity.getMember().getMemberId())
                .hospitalId(markerFavoritesEntity.getHospital().getHospitalId())
                .shelterId(markerFavoritesEntity.getShelter().getShelterId())
                .nickname(markerFavoritesEntity.getNickname())
                .build();
    }
}
