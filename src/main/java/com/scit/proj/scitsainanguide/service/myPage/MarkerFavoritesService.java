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
    public Page<MarkerFavoritesDTO> viewAllFavoritesMarkerTest(SearchRequestDTO dto) {
        return markerFavoritesRepository.selectMarkerFavoritesList(dto, "tsh0828");
    }

    public List<MarkerFavoritesDTO> viewAllFavoritesMarkerTest_NoPaging() {
        return markerFavoritesRepository.selectAllMarkerFavoritesDTO_NoPaging("tsh0828");
    }
    
    // 마커 주인하고 맞나 비교하는 코드
    public boolean isMarkerOwner(Integer myFavoriteId, String memberId) {
        MarkerFavoritesEntity markerFavoritesEntity = markerFavoritesJPARepository.findById(myFavoriteId ).orElseThrow(() -> new EntityNotFoundException(memberId));

        if(markerFavoritesEntity.getMember().getMemberId() == memberId) {
            return true;
        }
        return false;
    }

    public boolean deleteMarker(Integer myFavoriteId, String memberId) {
        if(isMarkerOwner(myFavoriteId, memberId)) {
            // delete
            return true;
        } else {
            // 걍 냅둠
            return false;
        }
    }

    // ==== TEST CODE ===============================================================
    /*public Page<MarkerFavoritesDTO> getList(int page, int pageSize)*/
    public Page<MarkerFavoritesDTO> getList(int page, int pageSize) {
        Pageable p = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "favoriteId");

        Page<MarkerFavoritesEntity> entities = markerFavoritesJPARepository.findAll(p);

        return entities.map(this::convertToDTO);
    }

    MarkerFavoritesDTO convertToDTO(MarkerFavoritesEntity entity) {
        return MarkerFavoritesDTO.builder()
                .favoriteId(entity.getFavoriteId())
                .memberId(entity.getMember().getMemberId())
                .hospitalId(entity.getHospital() != null ? entity.getHospital().getHospitalId() : null)
                .shelterId(entity.getShelter() != null ? entity.getShelter().getShelterId() : null)
                .nickname(entity.getNickname() != null ? entity.getNickname() : null)
                .build();
    }
    // ==============================================================================
}
