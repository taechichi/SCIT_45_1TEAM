package com.scit.proj.scitsainanguide.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scit.proj.scitsainanguide.domain.entity.ShelterEntity;
import com.scit.proj.scitsainanguide.repository.ShelterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShelterService {

    private final ShelterRepository shelterRepository;
    private final ObjectMapper objectMapper;

    public void importDataFromJsonFile(MultipartFile file) throws IOException {

        // 업로드된 파일의 InputStream을 사용하여 JSON 데이터를 읽음
        InputStream inputStream = file.getInputStream();
        JsonNode rootNode = objectMapper.readTree(inputStream);

        // 루트 노드를 로그로 출력하여 확인
        //log.debug("Root Node: {}", rootNode.toString());

        // JSON 배열 반복
        for (JsonNode node : rootNode) {

            // JSON 노드 전체를 로그로 출력
            //log.debug("Full JSON Node: {}", node.toString());

            // 키가 존재하는지 확인
            //log.debug("Facilities Name Key Exists: {}", node.has("施設・場所名"));

            // 키의 값을 로그로 출력
            //log.debug("name: {}", node.path("設・場所名").asText());

            ShelterEntity shelter = ShelterEntity.builder()
                    .shelterName(node.path("設・場所名").asText())
                    .latitude(node.path("緯度").asText())
                    .longitude(node.path("経度").asText())
                    .floodYn("1".equals(node.path("洪水").asText(""))) // "1"일 때 true, 아니면 false
                    .earthYn("1".equals(node.path("崖崩れ、土石流及び地滑り").asText("")))
                    .highTideYn("1".equals(node.path("高潮").asText("")))
                    .earthquakeYn("1".equals(node.path("地震").asText("")))
                    .tsunamiYn("1".equals(node.path("津波").asText("")))
                    .fireYn("1".equals(node.path("大規模な火事").asText("")))
                    .inlandFloodingYn("1".equals(node.path("内水氾濫").asText("")))
                    .volcanoYn("1".equals(node.path("火山現象").asText("")))
                    .langCd("ja") // 고정 값
                    .build();
            // DB에 저장
            shelterRepository.save(shelter);
        }
    }

    // 반경 내 대피소 정보 검색
    public List<ShelterEntity> findNearbyShelters(double latitude, double longitude) {
        // Repository를 호출하여 데이터베이스에서 대피소 정보 가져오기
        return shelterRepository.findNearbyShelters(latitude, longitude);
    }

    //쉘터 검색
    public ShelterEntity findByPlaceId(String placeId) {
        // placeId가 문자열일 경우, 정수로 변환
        Integer shelterId = Integer.valueOf(placeId);

        // 변환한 shelterId를 사용하여 데이터베이스에서 엔티티를 검색
        return shelterRepository.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException("조회한 쉘터가 DB에 없습니다"));
    }


}
