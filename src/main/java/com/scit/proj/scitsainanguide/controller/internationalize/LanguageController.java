package com.scit.proj.scitsainanguide.controller.internationalize;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;

@Controller
public class LanguageController {

    @GetMapping("/language")
    public String changeLanguage(@RequestParam String lang, HttpSession session, HttpServletRequest request) {
        // 언어 설정을 세션에 세팅한다.
        session.setAttribute("lang", lang);

        // Locale 객체 생성 및 설정
        Locale locale = Locale.forLanguageTag(lang);
        LocaleContextHolder.setLocale(locale);
        RequestContextUtils.getLocaleResolver(request).setLocale(request, null, locale);

        // Referer 헤더를 확인하여 이전 페이지 URL을 가져온다.
        String referer = request.getHeader("Referer");

        // Referer가 없으면 홈페이지로, 있으면 이전 페이지로 리다이렉트
        if (referer != null) {
            return "redirect:" + referer;
        } else {
            return "redirect:/";
        }
    }
}
