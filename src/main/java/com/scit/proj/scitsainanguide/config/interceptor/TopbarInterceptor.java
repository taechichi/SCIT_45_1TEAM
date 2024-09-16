package com.scit.proj.scitsainanguide.config.interceptor;

import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.util.TopbarUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class TopbarInterceptor implements HandlerInterceptor {

    private final TopbarUtils topbarUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // @ResponseBody가 붙은 Controller 메서드를 처리하는 요청인지 확인
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(ResponseBody.class)
                || !handlerMethod.hasMethodAnnotation(GetMapping.class)) {
                // @ResponseBody가 붙어있거나, @GetMapping이 붙어있지 않은 메서드에는 이 인터셉터를 적용하지 않을 것임
                return true;
            }
        }

        // 컨트롤러에 요청이 도달하기 전에 실행할 공통 로직
        ModelAndView modelAndView = new ModelAndView();
        AuthenticatedUser user = getAuthenticatedUser();
        modelAndView = topbarUtils.setTopbarFragmentData(user, modelAndView);

        // 미리 세팅한 modelAndView 객체를 HttpServletRequest 객체에 담아서 전달
        request.setAttribute("modelAndView", modelAndView);

        return true; // 요청을 계속 처리 (false 면 요청을 처리하지 않음.)
    }

    // 로그인된 사용자 정보를 가져오는 메서드
    private AuthenticatedUser getAuthenticatedUser() {
        // SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            return (AuthenticatedUser) authentication.getPrincipal();
        }
        return null;
    }
}
