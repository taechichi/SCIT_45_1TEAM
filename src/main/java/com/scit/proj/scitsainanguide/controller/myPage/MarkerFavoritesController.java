package com.scit.proj.scitsainanguide.controller.myPage;


import com.scit.proj.scitsainanguide.security.AuthenticatedUser;
import com.scit.proj.scitsainanguide.service.myPage.MarkerFavoritesService;
import com.scit.proj.scitsainanguide.util.PaginationUtils;
import com.scit.proj.scitsainanguide.util.TopbarUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("my/myMarkerFavorites")
public class MarkerFavoritesController {

    // ============== 초기화 목록 ==============
    private final MarkerFavoritesService markerFavoritesService;
    private final PaginationUtils paginationUtils;
    private final TopbarUtils topbarUtils;

    @Value("20")
    private int pageSize;
    // =======================================

    // ============ 처음에 들어오면 모든 정보 출력 ============
    @GetMapping("")
    public ModelAndView enterMarkerFavorites(
            @ModelAttribute SearchRequestDTO dto,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        log.debug("enterMarkerFavorites method called.");
        log.debug("CONTROLLER_SearchRequestDTO: {}", dto);
        dto.setPageSize(pageSize);
        if(dto.getSortBy() == null || dto.getSortBy().isEmpty()) {
            dto.setSortBy("sortByDistance");
        }
        // Service 에서 호출
        Page<MarkerFavoritesDTO> markerFavoritesAllList = markerFavoritesService.selectMarkerFavoritesFilterAndSearchList(dto, user.getUsername());
        ModelAndView modelAndView = paginationUtils.getPaginationData(markerFavoritesAllList, dto);
        modelAndView.addObject("pageData", markerFavoritesAllList);
        modelAndView.setViewName("myPage/myMarkerFavorites");
        topbarUtils.setTopbarFragmentData(user, modelAndView);  // topbar 관련 데이터 설정

        return modelAndView;
    }

    // ===================================================


    // ============ 닉네임 수정 기능 ============
    @PostMapping("/updateNickname")
    public String updateNickname(
            @RequestParam("favoriteId") Integer favoriteId,
            @RequestParam("nickname") String newNickname,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal AuthenticatedUser user
            ) {

        // 임시 계정
        String currentUserId = user.getUsername();

        log.debug("updateNickname method called.");
        log.debug("CONTROLLER_favoriteId: {}", favoriteId);
        log.debug("CONTROLLER_newNickname: {}", newNickname);

        try {
            // 닉네임 수정 서비스 호출
            markerFavoritesService.updateNickname(favoriteId, newNickname, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", "닉네임이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("닉네임 수정 중 오류 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "닉네임 수정 중 오류가 발생했습니다.");
        }

        return "redirect:/my/myMarkerFavorites";
    }
    // =========================================

    // ============ 삭제 기능 ============
    @PostMapping("/delete")
    public String deleteMarkerFavorites(
            @RequestParam(name = "favoriteId", required = false) List<Integer> favoriteIdsForDelete,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        // 임시 계정
        String currentUserId = user.getUsername();

        // favoriteIds가 비어있거나 null인 경우 처리
        if (favoriteIdsForDelete == null || favoriteIdsForDelete.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제할 마커를 선택하세요.");
            return "redirect:/my/myMarkerFavorites";  // 선택된 항목이 없을 때
        }

        // 비효율적이라 나중에 수정 예정
        for(Integer favoriteId : favoriteIdsForDelete) {
            markerFavoritesService.deleteMarker(favoriteId, currentUserId);
        }

        redirectAttributes.addFlashAttribute("successMessage", "선택된 마커가 삭제되었습니다.");
        return "redirect:/my/myMarkerFavorites";
    }
    // ==================================
}
