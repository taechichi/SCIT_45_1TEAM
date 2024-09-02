package com.scit.proj.scitsainanguide.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShelterDTO {

    private Integer shelterId;
    private String shelterName;
    private String latitude;
    private String longitude;
    private boolean floodYn;
    private boolean earthquakeYn;
    private boolean earthYn;
    private boolean highTideYn;
    private boolean tsunamiYn;
    private boolean fireYn;
    private boolean inlandFloodingYn;
    private boolean volcanoYn;
    private String lang_cd;

}
