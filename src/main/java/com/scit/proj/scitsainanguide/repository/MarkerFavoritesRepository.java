package com.scit.proj.scitsainanguide.repository;


import com.scit.proj.scitsainanguide.domain.dto.MarkerFavoritesDTO;
import com.scit.proj.scitsainanguide.domain.dto.SearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkerFavoritesRepository {

    Page<MarkerFavoritesDTO> selectMarkerFavoritesList(SearchRequestDTO dto, String memberId);

    Page<MarkerFavoritesDTO> selectMarkerFavoritesBySearchAndFilter(SearchRequestDTO dto, String tsh0828);
}
