package com.scit.proj.scitsainanguide.controller;


import com.scit.proj.scitsainanguide.domain.dto.ShelterDTO;
import com.scit.proj.scitsainanguide.domain.entity.ShelterEntity;
import com.scit.proj.scitsainanguide.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@RequestMapping("api")
@RequiredArgsConstructor
public class RestSearchController {

    private final ShelterService shelterService;


    @PostMapping("/api/nearby-shelters")
    public ResponseEntity<List<ShelterEntity>> getNearbyShelters(@RequestBody ShelterDTO request) {
        // 요청된 위치 정보를 기반으로 반경 내 대피소 정보 검색
        List<ShelterEntity> shelters = shelterService.findNearbyShelters(Double.parseDouble(request.getLatitude()), Double.parseDouble(request.getLongitude()));

        if (shelters.isEmpty()) {
            return ResponseEntity.noContent().build(); // 대피소가 없을 경우 204 응답
        } else {
            return ResponseEntity.ok(shelters); // 대피소 목록 반환
        }
    }

}
