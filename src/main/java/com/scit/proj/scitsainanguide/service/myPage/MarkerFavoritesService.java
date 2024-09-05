package com.scit.proj.scitsainanguide.service.myPage;

import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesJPARepository;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesRepository;
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

    public Page<MarkerFavoritesDTO> viewAllFavoritesMarkerTest(SearchRequestDTO dto) {
        return markerFavoritesRepository.selectMarkerFavoritesList(dto, "tsh0828");
    }

    public List<MarkerFavoritesDTO> viewAllFavoritesMarkerTest_NoPaging() {
        return markerFavoritesRepository.selectAllMarkerFavoritesDTO_NoPaging("tsh0828");
    }

    /*public Page<MarkerFavoritesDTO> viewAllFavoritesMarker(SearchRequestDTO dto, String memberId) {
        // === test 용 현재 로그인이 구현안되었기 때문 ===
        MemberDTO tempMember_forTest = new MemberDTO();
        //markerFavoritesRepository.selectMarkerFavoritesList()
    }*/

    public Page<MarkerFavoritesDTO> getList(int page, int pageSize) {
        Pageable p = PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "favoritesId");

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

}
