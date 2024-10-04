package com.scit.proj.scitsainanguide.service.myPage;

import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.domain.entity.*;
import com.scit.proj.scitsainanguide.repository.*;
import com.scit.proj.scitsainanguide.domain.entity.MarkerFavoritesEntity;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesJPARepository;
import com.scit.proj.scitsainanguide.repository.MarkerFavoritesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MarkerFavoritesService {

    private final MarkerFavoritesRepository markerFavoritesRepository;
    private final MarkerFavoritesJPARepository markerFavoritesJPARepository;
    private final MemberJpaRepository memberJpaRepository;
    private final HospitalRepository hospitalRepository;
    private final ShelterRepository shelterRepository;

    @PersistenceContext
    private EntityManager entityManager;

    //즐겨찾기 마커 추가 기능
    public void AddFavMarker(MarkerFavoritesDTO markerFavoritesDTO) throws IOException {
        MemberEntity memberEntity = memberJpaRepository.findById(markerFavoritesDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원아이디가 없습니다."));

        String placeId = markerFavoritesDTO.getHospitalId();
        MarkerFavoritesEntity markerFavoritesEntity = null;
        Optional<HospitalEntity> hospitalEntityOptional = hospitalRepository.findById(placeId);
        Optional<ShelterEntity> shelterEntityOptional= Optional.empty();

        // Optional이 true 일 경우
        if(hospitalEntityOptional.isPresent()) {
            HospitalEntity hospitalEntity = hospitalEntityOptional.get();
            markerFavoritesEntity = MarkerFavoritesEntity.builder()
                    .member(memberEntity)
                    .hospital(hospitalEntity)
                    .build();
        }else{
            shelterEntityOptional = shelterRepository.findById(Integer.valueOf(placeId));
            if(shelterEntityOptional.isPresent()){
                ShelterEntity shelterEntity = shelterEntityOptional.get();
                markerFavoritesEntity = MarkerFavoritesEntity.builder()
                        .member(memberEntity)
                        .shelter(shelterEntity)
                        .build();
            }else{
                log.debug("일치하는 ID가 없습니다.");
            }
        }
        if (markerFavoritesEntity != null) {
            markerFavoritesJPARepository.save(markerFavoritesEntity);
        } else {
            log.debug("markerFavoritesEntity가 null이어서 저장하지 않았습니다.");
        }
    }

    //즐겨찾기 여부 확인
    public boolean isFavorite(String memberId, String placeId) {
        log.debug("체크 place 값:{}", placeId);

        // 병원 ID 테이블에서 먼저 존재하는지 확인
        if (hospitalRepository.findById(placeId).isPresent()) {
            // 병원 ID로 검색
            return markerFavoritesJPARepository.existsByMemberIdAndHospitalId(memberId, placeId);
        }
        // 쉘터 ID로 검색
        return markerFavoritesJPARepository.existsByMemberIdAndShelterId(memberId, placeId);
    }




    public void removeFavorite(String memberId, String placeId) {
        // 병원 ID로 먼저 삭제 시도
        if (markerFavoritesJPARepository.existsByMemberIdAndHospitalId(memberId, placeId)) {
            markerFavoritesJPARepository.deleteByMemberIdAndHospitalId(memberId, placeId);  // 병원 ID로 삭제
            System.out.println("즐겨찾기가 병원에서 삭제되었습니다.");
        }
        // 병원 ID가 아니라면 쉘터 ID로 삭제 시도
        else if (markerFavoritesJPARepository.existsByMemberIdAndShelterId(memberId, placeId)) {
            markerFavoritesJPARepository.deleteByMemberIdAndShelterId(memberId, placeId);  // 쉘터 ID로 삭제
            System.out.println("즐겨찾기가 쉘터에서 삭제되었습니다.");
        }
        // 둘 다 없을 경우
        else {
            throw new EntityNotFoundException("해당 즐겨찾기가 존재하지 않습니다.");
        }
    }


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
