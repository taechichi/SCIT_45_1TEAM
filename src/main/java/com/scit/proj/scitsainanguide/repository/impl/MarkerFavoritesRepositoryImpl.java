package com.scit.proj.scitsainanguide.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.*;
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
    private QHospitalEntity hospitalEntity = QHospitalEntity.hospitalEntity;
    private QShelterEntity shelterEntity = QShelterEntity.shelterEntity;

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
        whereClause.and(markerFavoritesEntity.member.memberId.eq(memberId));    // 기본적으로 memberId로 검색

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
    public Page<MarkerFavoritesDTO> selectMarkerFavoritesBySearchAndFilter(SearchRequestDTO dto, String memberId) {
        log.debug("=== 1 === SearchRequestDTO : {}", dto);
        boolean isHospital = dto.getFilter().equals("hospital");
        boolean isShelter = dto.getFilter().equals("shelter");
        boolean isSortByDistance = dto.getSortBy().equals("sortByDistance");
        boolean isSortByName = dto.getSortBy().equals("sortByName");

        log.debug("=== 2 === isHospital : {}", isHospital);
        log.debug("=== 3 === isShelter : {}", isShelter);
        log.debug("=== 4 === isSortByDistance : {}", isSortByDistance);
        log.debug("=== 5 === isSortByName : {}", isSortByName);

        // 페이징 초기화
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        // QueryDSL의 동적 쿼리 생성을 위한 조건 빌더
        BooleanBuilder whereClause = new BooleanBuilder();
        // QueryDSL의 정렬 쿼리 생성을 위한 빌더
        OrderSpecifier<?> sortBy = null;
        // 로그인한사람의 모든 즐겨찾기 마커 긁어오기
        whereClause.and(markerFavoritesEntity.member.memberId.eq(memberId));

        // hospital과 shelter에 따라 latitude와 longitude를 설정하고, String을 Double로 캐스팅
        NumberTemplate<Double> latitudeExpression;
        NumberTemplate<Double> longitudeExpression;

        // hospital 필터(filter) 눌렀을 때,
        if(isHospital){
            log.debug("=== 6 === isHospitalFilter applied.");
            whereClause.and(markerFavoritesEntity.hospital.hospitalId.isNotEmpty());
        }
        // shelter 필터(filter)눌렀을 때,
        if(isShelter){
            log.debug("=== 7 === isShelterFilter applied.");
            whereClause.and(markerFavoritesEntity.shelter.shelterId.stringValue().isNotEmpty());
        }

        // 이름(searchWord)
        if(!Objects.equals(dto.getSearchWord(), "")) {
            if(isHospital) {
                log.debug("=== 8 === isHospitalFilter with searchWord applied.");
                log.debug("hospital&dto.getSearchWord(): {}", dto.getSearchWord());
                BooleanBuilder searchCondition = new BooleanBuilder();
                searchCondition.and(markerFavoritesEntity.hospital.hospitalName.contains(dto.getSearchWord()).or(markerFavoritesEntity.nickname.contains(dto.getSearchWord())));
                whereClause.and(searchCondition);
            } else if(isShelter) {
                log.debug("=== 9 === isShelterFilter with searchWord applied.");
                log.debug("shelter&dto.getSearchWord(): {}", dto.getSearchWord());
                BooleanBuilder searchCondition = new BooleanBuilder();
                searchCondition.and(markerFavoritesEntity.shelter.shelterName.contains(dto.getSearchWord()).or(markerFavoritesEntity.nickname.contains(dto.getSearchWord())));
                whereClause.and(searchCondition);
            } else {
                log.debug("=== 10 === Only searchWord applied.");
                log.debug("dto.getSearchWord() {}: ", dto.getSearchWord());
                BooleanBuilder searchCondition = new BooleanBuilder();

                // 검색어를 각 필드에 대해 or로 연결
                searchCondition.or(markerFavoritesEntity.nickname.contains(dto.getSearchWord()))
                        .or(markerFavoritesEntity.hospital.hospitalName.contains(dto.getSearchWord()))
                        .or(markerFavoritesEntity.shelter.shelterName.contains(dto.getSearchWord()));

                whereClause.and(searchCondition);
            }
        }

        log.debug("=== 11 === filter, searchWord Passed.");

        log.debug("=== 12 === sortBy Begin.");
        // hospital과 shelter에 따라 latitude와 longitude를 설정
        if (markerFavoritesEntity.hospital != null) {
            latitudeExpression = Expressions.numberTemplate(Double.class, "CAST({0} AS DOUBLE)", markerFavoritesEntity.hospital.latitude);
            longitudeExpression = Expressions.numberTemplate(Double.class, "CAST({0} AS DOUBLE)", markerFavoritesEntity.hospital.longitude);
        } else {
            latitudeExpression = Expressions.numberTemplate(Double.class, "CAST({0} AS DOUBLE)", markerFavoritesEntity.shelter.latitude);
            longitudeExpression = Expressions.numberTemplate(Double.class, "CAST({0} AS DOUBLE)", markerFavoritesEntity.shelter.longitude);
        }

        if(isSortByDistance) {
            NumberTemplate<Double> distanceExpression = Expressions.numberTemplate(
                    Double.class,
                    "({0} * cos(radians({1})) * cos(radians({2}))) + ({3} * cos(radians({1})) * sin(radians({2}))) + sin(radians({1})) * sin(radians({4}))",
                    Math.cos(Math.toRadians(dto.getLatitude())),
                    latitudeExpression,
                    longitudeExpression,
                    Math.sin(Math.toRadians(dto.getLatitude())),
                    latitudeExpression
            );

            sortBy = distanceExpression.asc();
        }

        if(isSortByName) {
            sortBy = new CaseBuilder()
                    .when(markerFavoritesEntity.nickname.isNotEmpty())
                    .then(markerFavoritesEntity.nickname)
                    .otherwise(
                            // 아래 표현은
                            // COALESCE(hospital_name, shelter_name) 이라는 쿼리를 생성함!
                            Expressions.stringTemplate("COALESCE({0}, {1})",
                                    markerFavoritesEntity.hospital.hospitalName,
                                    markerFavoritesEntity.shelter.shelterName)
                    ).asc();
        }
        log.debug("=== 13 === sortBy End.");


        // 리스트 업
        List<MarkerFavoritesEntity> markerFavoritesEntityList = queryFactory.selectFrom(markerFavoritesEntity)
                .leftJoin(markerFavoritesEntity.hospital, hospitalEntity)
                .leftJoin(markerFavoritesEntity.shelter, shelterEntity)
                .where(whereClause)
                // default 거리 기준으로 정렬, 이름 오름차순
                // 거리 기준으로 정렬
                // 1. 내 위치 - 각자 위치 거리
                .orderBy(sortBy)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(markerFavoritesEntity)
                .where(whereClause)
                .fetchCount();


        log.debug("==== 12 ==== Result list size: {}", markerFavoritesEntityList.size());
        log.debug("==== 13 ==== Total count: {}", total);
        log.debug("==== 14 Result: {}", markerFavoritesEntityList);

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
}
