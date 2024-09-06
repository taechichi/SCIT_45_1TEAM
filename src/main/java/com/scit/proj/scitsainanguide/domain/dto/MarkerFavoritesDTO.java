package com.scit.proj.scitsainanguide.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarkerFavoritesDTO {
    private Integer favoriteId;
    private String memberId;
    private Integer hospitalId;
    private Integer shelterId;
    private String nickname;
}
