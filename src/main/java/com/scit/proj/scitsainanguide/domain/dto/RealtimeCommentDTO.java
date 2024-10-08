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
    private String memberId;
    private Integer commentNum;
    private Integer replyNum;
    private String nickname;
    private String location;
    private String contents;
    private String stMessage;
    private String statusName;
    private LocalDateTime createDt;
}