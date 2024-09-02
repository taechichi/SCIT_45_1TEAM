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
}
