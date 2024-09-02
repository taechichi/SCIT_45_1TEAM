package com.scit.proj.scitsainanguide.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public class LanguageUtils {

    @Value("project.default.language")
    private String defaultLanguage;

    // 언어를 결정하는 메서드
    public String determineLanguage(HttpServletRequest request) {
        String languageHeader = request.getHeader("Accept-Language");
        if (StringUtils.hasLength(languageHeader)) {
            // 'Accept-Language' 헤더에서 첫 번째 언어 코드 추출
            return languageHeader.split(",")[0].split("-")[0]; // 예: 'ko', 'en', 'ja'
        } else {
            // 기본 언어 설정
            return defaultLanguage;
        }
    }
}
