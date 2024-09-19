package com.scit.proj.scitsainanguide.controller.myPage;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.myPage.MarkerFavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MarkerFavoritesRestController {

    private final MarkerFavoritesService markerFavoritesService;

    @PostMapping("/addFavorite/{placeId}")
    public ResponseEntity<String> addFavorite(@PathVariable("placeId") String placeId,
                                              @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            System.out.println("Received placeId: " + placeId);
            // DTO 생성 및 placeId 설정
            MarkerFavoritesDTO markerFavoritesDTO = new MarkerFavoritesDTO();
            markerFavoritesDTO.setHospitalId(placeId);
            markerFavoritesDTO.setMemberId(user.getUsername());

            // 서비스 호출하여 데이터 저장
            markerFavoritesService.AddFavMarker(markerFavoritesDTO);

            // 성공 응답 반환
            return ResponseEntity.ok("즐겨찾기에 추가되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            // 실패 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("즐겨찾기 추가에 실패했습니다.");
        }
    }

    @PostMapping("/isFavorite/{placeId}")
    public ResponseEntity<Boolean> checkFavorite(
            @PathVariable("placeId") String placeId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            // 인증된 사용자 ID를 가져오기
            String memberId = user.getUsername();

            // placeId와 인증된 사용자 ID를 사용하여 즐겨찾기 여부 확인
            boolean isFavorite = markerFavoritesService.isFavorite(memberId, placeId);

            return ResponseEntity.ok(isFavorite);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

}
