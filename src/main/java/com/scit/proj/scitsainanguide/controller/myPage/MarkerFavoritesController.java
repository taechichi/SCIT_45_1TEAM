package com.scit.proj.scitsainanguide.controller.myPage;


import com.scit.proj.scitsainanguide.service.myPage.MarkerFavoritesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("my/myMarkerFavorites")
public class MarkerFavoritesController {

    // ============== 초기화 목록 ==============
    private final MarkerFavoritesService markerFavoritesService;

    @Value("20")
    private int pageSize;
    @Value("1")
    private int page;
    // =======================================

    @GetMapping("")
    public String enterMarkerFavorites(
            /*@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,*/
            @ModelAttribute SearchRequestDTO dto,
            Model model
    ) {
        dto.setPageSize(pageSize);
        Page<MarkerFavoritesDTO> markerFavoritesDTOPage = markerFavoritesService.viewAllFavoritesMarkerTest(dto);

        log.debug("markerFavoritesDTOPage: {}", markerFavoritesDTOPage);
        model.addAttribute("markerFavoritesDTOPage", markerFavoritesDTOPage);
        model.addAttribute("page", dto.getPage());

        return "myPage/myMarkerFavorites";
    }

    @PostMapping("/delete")
    public String deleteMarkerFavorites(
            @RequestParam(name = "favoriteId") Integer myFavoriteId
    ) {
        // 임시 계정
        String currentUserId = "tsh0828";
        markerFavoritesService.deleteMarker(myFavoriteId, currentUserId);
        return "redirect:/my/myMarkerFavorites";
    }



    // ====================================================
    // =============== MY FAVORITES MARKERS ===============
    // =============== 즐겨 찾기 마커 관련 함수 ===============
    // ====================================================

    // ==== Initial Page ====
    /*@GetMapping("")
    public String MarkerFavoritesPageEnter(
            @ModelAttribute SearchRequestDTO dto,
            Model model
    ) {

        if(dto.getPage() < 1) {
            dto.setPage(1);
        }
        dto.setPage(dto.getPage() - 1);

        if(dto.getPageSize() <= 0 ) {
            dto.setPageSize(pageSize);
        }

        // ==== View all favorites marker ====
        dto.setPage(page);
        dto.setPageSize(pageSize);
        Page<MarkerFavoritesDTO> markerFavoritesDTOPage = markerFavoritesService.viewAllFavoritesMarkerTest(dto);

        log.debug("markerFavoritesDTOPage: {}", markerFavoritesDTOPage);
        model.addAttribute("markerFavoritesDTOPage", markerFavoritesDTOPage);
        // =======================================
        return "/myPage/myMarkerFavorites";
    }*/

    // pracController

    // test
    /*@GetMapping("/myMarkerFavorites")
    public String MarkerFavoritesPageEnter(
            Model model
    ) {

        // ==== View all favorites marker ====
        // ==== 모든 즐겨찾기 마커 보기 ====
        // ==== 계정에 따른 리스트업은 아직 미구현 ====
        List<MarkerFavoritesDTO> markerFavoritesDTOList = markerFavoritesService.viewAllFavoritesMarkerTest_NoPaging();

        log.debug("markerFavoritesDTOList: {}", markerFavoritesDTOList);
        model.addAttribute("markerFavoritesDTOList", markerFavoritesDTOList);
        // =======================================
        return "/myPage/myMarkerFavorites";
    }*/
    // ======================

}
