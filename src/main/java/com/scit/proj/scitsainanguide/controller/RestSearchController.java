package com.scit.proj.scitsainanguide.controller;


import com.scit.proj.scitsainanguide.domain.dto.ShelterDTO;
import com.scit.proj.scitsainanguide.domain.entity.ShelterEntity;
import com.scit.proj.scitsainanguide.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class RestSearchController {

    private final ShelterService shelterService;


    @PostMapping("/nearby-shelters")
    public ResponseEntity<List<ShelterEntity>> getNearbyShelters(@RequestBody ShelterDTO request) {
        // 요청된 위치 정보를 기반으로 반경 내 대피소 정보 검색
        List<ShelterEntity> shelters = shelterService.findNearbyShelters(Double.parseDouble(request.getLatitude()), Double.parseDouble(request.getLongitude()));

        if (shelters.isEmpty()) {
            return ResponseEntity.noContent().build(); // 대피소가 없을 경우 204 응답
        } else {
            return ResponseEntity.ok(shelters); // 대피소 목록 반환
        }
    }


    // GET 방식으로 요청을 처리하여 대피소 정보를 반환
    @GetMapping("/shelter/{placeId}")
    public ResponseEntity<ShelterEntity> getShelterByPlaceId(@PathVariable("placeId") String placeId) {
        // 서비스에서 placeId를 사용하여 대피소 엔티티를 찾음
        ShelterEntity shelter = shelterService.findByPlaceId(placeId);

        // 엔티티가 존재하면 DTO로 변환 후 반환
            return ResponseEntity.ok(shelter);  // 200 OK와 함께 DTO 반환
        }

}
