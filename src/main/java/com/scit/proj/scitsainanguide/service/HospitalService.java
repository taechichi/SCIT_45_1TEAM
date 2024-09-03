package com.scit.proj.scitsainanguide.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scit.proj.scitsainanguide.domain.entity.HospitalEntity;
import com.scit.proj.scitsainanguide.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
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

            HospitalEntity hospitalEntity = HospitalEntity.builder()
                    .hospitalName(node.path("設・場所名").asText())
                    .latitude(node.path("緯度").asText())
                    .longitude(node.path("経度").asText())
                    .langCd("ja") // 고정 값*/
                    .build();
            // DB에 저장
            hospitalRepository.save(hospitalEntity);
        }
    }
}
