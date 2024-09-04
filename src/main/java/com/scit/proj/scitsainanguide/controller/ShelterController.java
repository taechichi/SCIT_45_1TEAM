package com.scit.proj.scitsainanguide.controller;

import com.scit.proj.scitsainanguide.service.ShelterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/Data/shelter")
@RequiredArgsConstructor
@Slf4j
public class ShelterController {

    private final ShelterService service;

    @GetMapping("")
    public String enterShelter() {
        log.debug("enterShelter method called.");
        return "DataSetup/shelter_info_setup";
    }

    // 새로운 메서드: locationService.importDataFromJsonFile_2() 호출
    @ResponseBody
    @PostMapping("/import-file")
    public String importShelterData(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // 이거 열면 13만개의 로그가 찍힐것이다. 절대 풀지말길 :)
            //log.debug("File uploaded: " + file.getOriginalFilename());
            service.importDataFromJsonFile(file);  // 메서드 호출
            return "데이터가 성공적으로 추가되었습니다.";
        } catch (IOException e) {
            log.error("파일 처리 중 오류 발생: {}", e.getMessage());
            return "데이터 가져오기 실패: " + e.getMessage();
        }
    }
}
