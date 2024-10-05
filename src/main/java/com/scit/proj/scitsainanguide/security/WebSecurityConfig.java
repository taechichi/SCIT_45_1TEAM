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
            , "/**"         // 테스트를 위해 모든 경로를 열어놓음. 필요하다면 지워서 사용하세요.
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
            //요청에 대한 권한 설정
            .authorizeHttpRequests(author -> author
                .requestMatchers(PUBLIC_URLS).permitAll()   //모두 접근 허용
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // 관리자 권한 체크
                .anyRequest().authenticated()               //그 외의 모든 요청은 인증 필요
            )
            //HTTP Basic 인증을 사용하도록 설정
            .httpBasic(Customizer.withDefaults())
            //폼 로그인 설정
            .formLogin(formLogin -> formLogin
                    .loginPage("/member/login")                //로그인폼 페이지 경로
                    .usernameParameter("memberId")                //폼의 ID 파라미터 이름
                    .passwordParameter("password")          //폼의 비밀번호 파라미터 이름
                    .loginProcessingUrl("/member/login")           //로그인폼 제출하여 처리할 경로
                    .defaultSuccessUrl("/")                 //로그인 성공 시 이동할 경로
                    .permitAll()                            //로그인 페이지는 모두 접근 허용
            )
            //로그아웃 설정
            .logout(logout -> logout
                    .logoutUrl("/member/logout")                   //로그아웃 처리 경로
                    .logoutSuccessUrl("/")                  //로그아웃 성공 시 이동할 경로
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
