package com.scit.proj.scitsainanguide.util;

import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class PaginationUtils {

    public ModelAndView getPaginationData(Page<?> pageData, SearchRequestDTO dto) {
        int totalPages = pageData.getTotalPages();
        int currentPage = dto.getPage();
        int startPage = Math.max(1, currentPage - 4);
        int endPage = Math.min(startPage + 4, totalPages);

        // 전체 페이지 수가 5페이지 이하인 경우
        if (totalPages <= 5) {
            startPage = 1;
            endPage = totalPages;
        }

        // ModelAndView 객체 생성
        ModelAndView modelAndView = new ModelAndView();

        // 모델에 데이터 세팅
        modelAndView.addObject("startPage", startPage);
        modelAndView.addObject("endPage", endPage);
        modelAndView.addObject("page", currentPage);
        modelAndView.addObject("filter", dto.getFilter());
        modelAndView.addObject("filterWord", dto.getFilterWord());
        modelAndView.addObject("searchType", dto.getSearchType());
        modelAndView.addObject("searchWord", dto.getSearchWord());

        // 뷰 이름 설정 (뷰의 이름은 여기서 설정할 필요는 없지만, 예시로 추가)
        // modelAndView.setViewName("viewName");

        return modelAndView; // 페이징 데이터와 모델 반환
    }
}
