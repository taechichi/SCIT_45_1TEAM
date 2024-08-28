package com.scit.proj.scitsainanguide.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final MessageSource ms;

    @GetMapping("test")
    public String test(Model model) {
        // 국제화 샘플
        model.addAttribute("name", "고니고니");
        model.addAttribute("city", "東京");
        return "test";
    }

    @GetMapping("error/{error}")
    public String error(@PathVariable String error) {
        if ("true".equals(error)) {
            throw new EntityNotFoundException("존재하지 않는 엔티티입니다.");
        }
        return "test";
    }
}
