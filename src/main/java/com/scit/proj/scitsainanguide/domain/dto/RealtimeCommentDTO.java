package com.scit.proj.scitsainanguide.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeCommentDTO {
    private Integer commentNum;
    private Integer replyNum;
    private String nickname;
    private String location;
    private String contents;
    private LocalDateTime createDt;
}