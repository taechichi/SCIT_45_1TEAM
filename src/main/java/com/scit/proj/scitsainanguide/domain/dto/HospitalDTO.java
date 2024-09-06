package com.scit.proj.scitsainanguide.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDTO {

    private String hospitalId;
    private Integer categoryId;
    private String hospitalName;
    private String longitude;
    private String latitude;
    private String lang_cd;
    private String businessHours;
}
