package com.scit.proj.scitsainanguide.controller.main;

import com.scit.proj.scitsainanguide.util.TopbarUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final TopbarUtils topbarUtils;

    /**
     * 메인 페이지 이동
     * @return 메인 페이지에 보낼 모델과 뷰 정보 객체
     */
    @GetMapping
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = (ModelAndView) request.getAttribute("modelAndView");
        modelAndView.setViewName("index");
        return modelAndView;
    }

}
