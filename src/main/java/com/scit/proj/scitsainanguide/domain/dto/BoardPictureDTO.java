package com.scit.proj.scitsainanguide.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardPictureDTO {
    private String path;        // 파일 경로
    private String oriFilename; // 원본 파일명
    private String newFilename; // UUID 으로 저장된 파일명
}
