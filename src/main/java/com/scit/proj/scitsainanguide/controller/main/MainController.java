package com.scit.proj.scitsainanguide.controller.main;

import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.main.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MainService mainService;

    /**
     * 메인 페이지 이동
     * @param user 로그인한 유저 정보 객체
     * @return 메인 페이지에 보낼 모델과 뷰 정보 객체
     */
    @GetMapping
    public ModelAndView index(@AuthenticationPrincipal AuthenticatedUser user) {
        return mainService.selectIndexPageData(user);
    }

}
