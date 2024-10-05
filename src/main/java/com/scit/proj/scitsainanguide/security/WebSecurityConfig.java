package com.scit.proj.scitsainanguide.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Security에 로그인 없이 접근할 수 있는 것들을 지정하는 에노테이션
@EnableWebSecurity
public class WebSecurityConfig {
    //로그인 없이 접근 가능 경로
    private static final String[] PUBLIC_URLS = {
            "/"                //메인화면
            , "/member/register"//로그인 없이 접근할 수 있는 페이지
            , "/member/login"
            , "/member/idCheck"
            , "/img/**"			//와일드카드 사용, 스테틱 아래 파일은 전부 허용
            , "/css/**"
            , "/js/**"
            , "/thymeleaf"
            , "/scss/**"
            , "/vendor/**"
            , "/comments/stream"
            , "/comments"
            //, "/**"         // 테스트를 위해 모든 경로를 열어놓음. 필요하다면 지워서 사용하세요.
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(author -> author
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(formLogin -> formLogin
                        .loginPage("/member/login")
                        .usernameParameter("memberId")
                        .passwordParameter("password")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/")  // 403 에러 시 메인 화면으로 리다이렉트
                );

        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }


    //비밀번호 암호화를 위한 인코더를 빈으로 등록, 빈은 객체를 생성해서 메모리에 올려두는 것
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

