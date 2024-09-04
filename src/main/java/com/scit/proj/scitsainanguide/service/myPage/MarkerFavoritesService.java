package com.scit.proj.scitsainanguide.service.myPage;

import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MarkerFavoritesService {

    private final MarkerFavoritesRepository markerFavoritesRepository;

    public Page<MarkerFavoritesDTO> viewAllFavoritesMarkerTest(SearchRequestDTO dto) {
        return markerFavoritesRepository.selectMarkerFavoritesList(dto, "tsh0828");
    }

    /*public Page<MarkerFavoritesDTO> viewAllFavoritesMarker(SearchRequestDTO dto, String memberId) {
        // === test 용 현재 로그인이 구현안되었기 때문 ===
        MemberDTO tempMember_forTest = new MemberDTO();
        //markerFavoritesRepository.selectMarkerFavoritesList()
    }*/
}
