package com.scit.proj.scitsainanguide.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VideoController {

    @GetMapping("video")
    public ModelAndView video(HttpServletRequest request) {
        ModelAndView modelAndView = (ModelAndView) request.getAttribute("modelAndView");
        modelAndView.setViewName("video");
        return modelAndView;
    }

}
