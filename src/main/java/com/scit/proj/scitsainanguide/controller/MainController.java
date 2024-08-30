package com.scit.proj.scitsainanguide.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * 메인 페이지 이동
     */
    @GetMapping
    public String index() {
        return "index";
    }
}
