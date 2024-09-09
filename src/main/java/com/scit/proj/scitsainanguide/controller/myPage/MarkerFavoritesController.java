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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // ============ 처음에 들어오면 모든 정보 출력 ============
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
    // ===================================================



    // ============ 삭제 기능 ============
    @PostMapping("/delete")
    public String deleteMarkerFavorites(
            @RequestParam(name = "favoriteId", required = false) List<Integer> favoriteIdsForDelete,
            RedirectAttributes redirectAttributes
    ) {
        // 임시 계정
        String currentUserId = "tsh0828";

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
