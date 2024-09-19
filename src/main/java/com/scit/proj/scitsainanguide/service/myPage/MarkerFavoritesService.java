package com.scit.proj.scitsainanguide.service.myPage;

import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesJPARepository;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesRepository;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MarkerFavoritesService {

    private final MarkerFavoritesRepository markerFavoritesRepository;
    private final MarkerFavoritesJPARepository markerFavoritesJPARepository;

    @PersistenceContext
    private EntityManager entityManager;

    // tsh0828의 마커 냅다 가져오는 코드
    // 나중에 ID 받는걸로 수정 해야함.
    public Page<MarkerFavoritesDTO> viewAllFavoritesMarkerTest(SearchRequestDTO dto) {
        return markerFavoritesRepository.selectMarkerFavoritesList(dto, "tsh0828");
    }

    // tsh0828의 마커 냅다 가져오긴하는데 검색하거나 필터 사용해서 가져오는 사비스
    // 나중에 ID 받는걸로 수정 해야함.
    public Page<MarkerFavoritesDTO> selectMarkerFavoritesFilterAndSearchList(SearchRequestDTO dto, String userId) {
        log.debug("Service_SearchRequestDTO: {}", dto);
        return markerFavoritesRepository.selectMarkerFavoritesBySearchAndFilter(dto, userId);
    }

    // 마커 삭제 기능
    public boolean deleteMarker(Integer myFavoriteId, String memberId) {
        if(isMarkerOwner(myFavoriteId, memberId)) {
            markerFavoritesJPARepository.deleteById(myFavoriteId);
            return true;
        }
        return false;
    }

    // 마커 수정 기능
    @Transactional
    public boolean updateNickname(Integer favoriteId, String newNickname, String currentUserId) {
        log.debug("SERVICE_favoriteId: {}", favoriteId);
        log.debug("SERVICE_newNickname: {}", newNickname);
        log.debug("SERVICE_currentUserId: {}", currentUserId);

        if(isMarkerOwner(favoriteId, currentUserId)) {

            MarkerFavoritesEntity tempEntity = markerFavoritesJPARepository.findById(favoriteId).orElseThrow(()-> new EntityNotFoundException("Not Exists Entity."));

            // 로그 추가
            log.debug("Before saving: {}", tempEntity);

            if(newNickname == null || newNickname.isEmpty()) {
                tempEntity.setNickname(null);
                return true;
            } else {
                tempEntity.setNickname(newNickname);
            }

            markerFavoritesJPARepository.save(tempEntity);

            // 로그 추가
            log.debug("After saving: {}", tempEntity);
            return true;
        }
        return false;
    }

    // ============= PRIVATE CODE =============
    // 마커 주인하고 맞나 비교하는 코드
    private boolean isMarkerOwner(Integer myFavoriteId, String memberId) {
        MarkerFavoritesEntity markerFavoritesEntity = markerFavoritesJPARepository.findById(myFavoriteId ).orElseThrow(() -> new EntityNotFoundException(memberId));

        if(Objects.equals(markerFavoritesEntity.getMember().getMemberId(), memberId)) {
            log.debug("주인님이 맞습니다.");
            return true;
        }
        log.debug("당신뭐야");
        return false;
    }
    // ========================================
}
