package com.scit.proj.scitsainanguide.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagCategoryDTO {
    private Integer tagId;
    private String contents;
    private String langCd;
}
