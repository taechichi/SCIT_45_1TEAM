package com.scit.proj.scitsainanguide.service.myPage;

import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesJPARepository;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MarkerFavoritesService {

    private final MarkerFavoritesRepository markerFavoritesRepository;
    private final MarkerFavoritesJPARepository markerFavoritesJPARepository;

    // tsh0828의 마커 냅다 가져오는 코드
    // 나중에 ID 받는걸로 수정 해야함.
    public Page<MarkerFavoritesDTO> viewAllFavoritesMarkerTest(SearchRequestDTO dto) {
        return markerFavoritesRepository.selectMarkerFavoritesList(dto, "tsh0828");
    }

    // tsh0828의 마커 냅다 가져오긴하는데 검색하거나 필터 사용해서 가져오는 사비스
    // 나중에 ID 받는걸로 수정 해야함.
    public Page<MarkerFavoritesDTO> selectMarkerFavoritesFilterAndSearchList(SearchRequestDTO dto) {
        log.debug("Service_SearchRequestDTO: {}", dto);
        return markerFavoritesRepository.selectMarkerFavoritesBySearchAndFilter(dto, "tsh0828");
    }

    // 마커 주인하고 맞나 비교하는 코드
    public boolean isMarkerOwner(Integer myFavoriteId, String memberId) {
        MarkerFavoritesEntity markerFavoritesEntity = markerFavoritesJPARepository.findById(myFavoriteId ).orElseThrow(() -> new EntityNotFoundException(memberId));

        if(markerFavoritesEntity.getMember().getMemberId() == memberId) {
            return true;
        }
        return false;
    }

    // 마커
    public boolean deleteMarker(Integer myFavoriteId, String memberId) {
        if(isMarkerOwner(myFavoriteId, memberId)) {
            // delete
            return true;
        } else {
            // 걍 냅둠
            return false;
        }
    }

}
