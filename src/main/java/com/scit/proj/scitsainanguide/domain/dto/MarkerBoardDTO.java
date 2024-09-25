package com.scit.proj.scitsainanguide.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkerBoardDTO {

    private Integer boardId;
    private String memberId;
    private String shelterName;
    private String hospitalName;
    private String contents;
    private LocalDateTime createDt;
    private String deleteReason;
    private Boolean deleteYn;
    private LocalDateTime deleteDt;
    private List<BoardPictureDTO> pictures;
}
