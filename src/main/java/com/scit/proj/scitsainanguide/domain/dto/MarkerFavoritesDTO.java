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
    // Variables associated with entitiy
    private Integer favoriteId;
    private String memberId;
    private String hospitalId;
    private Integer shelterId;
    private String nickname;

    // etc
    private String name;
}
