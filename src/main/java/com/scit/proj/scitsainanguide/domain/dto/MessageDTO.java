package com.scit.proj.scitsainanguide.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {

    private Integer messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime createDt;
    private Boolean deleteYn;

    // 내 쪽지 목록 출력에 필요한 필드
    private String fileName;

    // 내 쪽지 목록 출력에 필요한 DTO 생성자
    public MessageDTO(Integer messageId, String senderId, String content, LocalDateTime createDt, String fileName) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.createDt = createDt;
        this.fileName = fileName;
    }
}
