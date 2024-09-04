package com.scit.proj.scitsainanguide.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkerBoardDTO {

    private Integer boardId;
    private String memberId;
    private Integer shelterId;
    private Integer hospitalId;
    private String contents;
    private LocalDateTime createDt;
    private Boolean deleteYn;
}
