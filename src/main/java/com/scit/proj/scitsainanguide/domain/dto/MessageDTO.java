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
    private LocalDateTime readDt;

    // 내 쪽지 목록 출력에 필요한 필드
    private String fileName;

    // topbar 에서 사용할 값들
    private Long unreadMessageCnt;
    private Integer statusId;

    // 내 쪽지 목록 출력에 필요한 DTO 생성자
    public MessageDTO(Integer messageId, String senderId, String content, LocalDateTime createDt, String fileName) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.createDt = createDt;
        this.fileName = fileName;
    }

    // 내 쪽지 상세보기에 필요한 DTO 생성자
    public MessageDTO(Integer messageId, String senderId, String receiverId
            , String content, LocalDateTime createDt, boolean deleteYn, String fileName) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.deleteYn = deleteYn;
        this.createDt = createDt;
        this.fileName = fileName;
    }

    // 내가 읽지 않은 쪽지 목록 출력(홈 화면)에 필요한 DTO 생성자
    public MessageDTO(Integer messageId, String senderId, Integer statusId, String content, LocalDateTime createDt, LocalDateTime readDt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.statusId = statusId;
        this.content = content;
        this.createDt = createDt;
        this.readDt = readDt;
    }
}
