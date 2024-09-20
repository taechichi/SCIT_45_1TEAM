package com.scit.proj.scitsainanguide.controller;


import com.scit.proj.scitsainanguide.domain.entity.HospitalEntity;
import com.scit.proj.scitsainanguide.service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RestHospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/get/hospitals")
    public List<HospitalEntity>getAllInfo(){
        return  hospitalService.getAllHospitals();
    }
}
