package com.scit.proj.scitsainanguide.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final MessageSource ms;

    @GetMapping("test")
    public String test() {
        // 국제화 샘플
        return "test";
    }
}
