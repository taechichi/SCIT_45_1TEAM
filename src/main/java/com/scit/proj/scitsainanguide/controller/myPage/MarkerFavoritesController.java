package com.scit.proj.scitsainanguide.controller.myPage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import com.scit.proj.scitsainanguide.service.myPage.MarkerFavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("my")
public class MarkerFavoritesController {

    // ============== 초기화 목록 ==============
    private final MarkerFavoritesService markerFavoritesService;
    // =======================================

    // ====================================================
    // =============== MY FAVORITES MARKERS ===============
    // =============== 즐겨 찾기 마커 관련 함수 ===============
    // ====================================================

    // ==== Initial Page ====
    @GetMapping("/myMarkerFavorites")
    public String MarkerFavoritesPageEnter(
            @ModelAttribute SearchRequestDTO dto,
            Model model
            ) {

        // ==== View all favorites marker ====
        // ==== 모든 즐겨찾기 마커 보기 ====
        // ==== 계정에 따른 리스트업은 아직 미구현 ====
        dto.setPageSize(20);
        Page<MarkerFavoritesDTO> markerFavoritesDTOPage = markerFavoritesService.viewAllFavoritesMarkerTest(dto);
        model.addAttribute("markerFavoritesDTOPage", markerFavoritesDTOPage);
        // =======================================
        return "/myPage/myMarkerFavorites";
    }
    // ======================
}
