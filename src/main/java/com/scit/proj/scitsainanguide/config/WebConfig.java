package com.scit.proj.scitsainanguide.config;

import com.scit.proj.scitsainanguide.config.interceptor.TopbarInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TopbarInterceptor topbarInterceptor;

    // 병원, 대피소 db저장을 위한 web 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .resourceChain(false);
    }

    // interceptor 관련 설정
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] allowedPatterns = {
                "/**" // 모든 요청 허용
        };
        String[] excludedPatterns = {
                // 스태틱 파일은 전부 제외
                "/img/**"
                , "/css/**"
                , "/js/**"
                , "/thymeleaf"
                , "/scss/**"
                , "/vendor/**"
                , "/admin/**"    // 관리자 관련 요청들
        };

        registry.addInterceptor(topbarInterceptor)
                .addPathPatterns(allowedPatterns)     // 적용할 경로
                .excludePathPatterns(excludedPatterns); // 제외할 경로
    }
}
