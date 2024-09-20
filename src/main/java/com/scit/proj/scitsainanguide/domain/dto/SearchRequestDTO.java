package com.scit.proj.scitsainanguide.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private String filter = "";

    @Builder.Default
    private String filterWord = "";

    @Builder.Default
    private String searchType = "";

    @Builder.Default
    private String searchWord = "";

    @Builder.Default
    private String sortBy = "";

    @Builder.Default
    private Double longitude = 0.0;

    @Builder.Default
    private Double latitude = 0.0;



    // 따로 셋팅해주어야하는 값
    private int pageSize;
}
