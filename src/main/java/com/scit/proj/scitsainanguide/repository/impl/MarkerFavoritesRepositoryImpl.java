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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@Transactional
public class MarkerFavoritesRepositoryImpl implements MarkerFavoritesRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final JPAQueryFactory queryFactory;
    private QMarkerFavoritesEntity markerFavoritesEntity = QMarkerFavoritesEntity.markerFavoritesEntity;

    // === CONSTRUCTOR ===
    @Autowired
    public MarkerFavoritesRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    // ===== with paging list =====
    // ===== View All list =====
    public Page<MarkerFavoritesDTO> selectMarkerFavoritesList(SearchRequestDTO dto, String memberId) {
        // PageRequest 객체를 생성하여 페이지 번호와 페이지 크기 설정
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());

        // QueryDSL의 동적 쿼리 생성을 위한 조건 빌더
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(markerFavoritesEntity.member.memberId.eq(memberId));

        // 리스트업
        List<MarkerFavoritesEntity> markerFavoritesEntityList = queryFactory.selectFrom(markerFavoritesEntity)
                .where(whereClause)
                // 이거 거리순으로 정렬하는거 해야함.
                .orderBy(markerFavoritesEntity.favoriteId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 ( 페이지을 위한 total count) -> 길이가 나와야 인수로 던져줄 수 있기 때문
        long total = queryFactory.selectFrom(markerFavoritesEntity)
                .where(whereClause)
                .fetchCount();

        // Entity -> DTO 변환 및 Page 변환
        List<MarkerFavoritesDTO> tempMarkerFavoritesDTOList = markerFavoritesEntityList.stream()
                .map(this::convertToMarkerFavoritesDTO)
                .toList();

        return new PageImpl<>(tempMarkerFavoritesDTOList, pageable, total);
    }

    // 장소 이름, 별칭 검색창에 누를때마다 호출해서 가져오도록
    //
    public Page<MarkerFavoritesDTO> selectMarkerFavoriteBySearchAndFilter(SearchRequestDTO dto, String memberId) {
        boolean isHospital = dto.getFilter().equals("hospital");
        boolean isShelter = dto.getFilter().equals("shelter");

        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());

        // QueryDSL의 동적 쿼리 생성을 위한 조건 빌더
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(markerFavoritesEntity.member.memberId.eq(memberId));

        if(isHospital){
            whereClause.and(markerFavoritesEntity.hospital.hospitalId.isNotEmpty());
        }

        if(isShelter){
            whereClause.and(markerFavoritesEntity.shelter.shelterId.isNotNull());
        }

        if(!Objects.equals(dto.getSearchWord(), "")) {
            if(isHospital) {
                whereClause.and(markerFavoritesEntity.hospital.hospitalName.contains(dto.getSearchWord()))
                        .or(markerFavoritesEntity.nickname.contains(dto.getSearchWord()));
            }

            if(isShelter) {
                whereClause.and(markerFavoritesEntity.shelter.shelterName.contains(dto.getSearchWord()))
                        .or(markerFavoritesEntity.nickname.contains(dto.getSearchWord()));
            }
        }

        // 리스트 업
        List<MarkerFavoritesEntity> markerFavoritesEntityList = queryFactory.selectFrom(markerFavoritesEntity)
                .where(whereClause)
                .orderBy(markerFavoritesEntity.favoriteId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(markerFavoritesEntity)
                .where(whereClause)
                .fetchCount();

        List<MarkerFavoritesDTO> tempMarkerFavoritesDTOList = markerFavoritesEntityList.stream().map(this::convertToMarkerFavoritesDTO).toList();

        return new PageImpl<>(tempMarkerFavoritesDTOList, pageable, total);
    }

    // 5. 마커 목록 검색 (장소 이름 / 분류 / 별칭)
    // 7. 마커 목록을 정렬한다. (기본: 거리 오름차순)
    // 8. 마커 목록 정렬 기준을 선택해, 해당 마커를 출력한다. (거리 오름차순, 이름 오름차순)

    // =================================================================================
    // ====== INNER FUNCTION ===========================================================
    private MarkerFavoritesDTO convertToMarkerFavoritesDTO(MarkerFavoritesEntity markerFavoritesEntity) {
        return MarkerFavoritesDTO.builder()
                .favoriteId(markerFavoritesEntity.getFavoriteId())
                .memberId(markerFavoritesEntity.getMember().getMemberId())
                .hospitalId(markerFavoritesEntity.getHospital() != null ? markerFavoritesEntity.getHospital().getHospitalId() : null)
                .shelterId(markerFavoritesEntity.getShelter() != null ? markerFavoritesEntity.getShelter().getShelterId() : null)
                .nickname(markerFavoritesEntity.getNickname())
                .name(markerFavoritesEntity.getHospital() != null ? markerFavoritesEntity.getHospital().getHospitalName() : markerFavoritesEntity.getShelter().getShelterName())
                .build();
    }
    // =================================================================================

    // =================================================================================
    // ====== TEST AREA ================================================================
    // ===== Without paging list ======
    public List<MarkerFavoritesDTO> selectAllMarkerFavoritesDTO_NoPaging(String memberId) {
        // 리스트업
        List<MarkerFavoritesEntity> markerFavoritesEntityList = queryFactory.selectFrom(markerFavoritesEntity)
                .fetch();

        return markerFavoritesEntityList.stream()
                .map(this::convertToMarkerFavoritesDTO)
                .toList();
    }
    // =================================================================================

}
