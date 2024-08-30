package com.scit.proj.scitsainanguide.domain.dto;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

    private String memberId;
    private Integer statusId;
    private String password;
    private String nickname;
    private String email;
    private String gender;
    private String phone;
    private String nationality;
    private Boolean adminYn;
    private Boolean withdraw;
    private LocalDateTime lastLoginDt;
    private LocalDateTime lastStUpdateDt;
    private String fileName;
    private String stMessage;
    private LocalDateTime endTime;
}

