package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
@Slf4j
public class HospitalController {

    private final HospitalService service;

    // 새로운 메서드: locationService.importDataFromJsonFile_2() 호출
    @PostMapping("/import-file")
    public String importHospitalData(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            service.importDataFromJsonFile(file);  // 메서드 호출
            return "데이터가 성공적으로 추가되었습니다.";
        } catch (IOException e) {
            log.error("파일 처리 중 오류 발생: {}", e.getMessage());
            return "데이터 가져오기 실패: " + e.getMessage();
        }
    }
}
