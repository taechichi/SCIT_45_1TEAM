package com.scit.proj.scitsainanguide.domain.dto;

import lombok.*;

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
    private Boolean favoriteYn;

    // Status 명을 직접 구해서 넣을 경우
    private String statusName;

    public MemberDTO(String memberId, String nickname, LocalDateTime lastStUpdateDt, String statusName) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.lastStUpdateDt = lastStUpdateDt;
        this.statusName = statusName;
    }

    public MemberDTO(String memberId, String nickname, String fileName, Integer statusId, String statusName) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.fileName = fileName;
        this.statusId = statusId;
        this.statusName = statusName;
    }

    public MemberDTO(String memberId, String nickname, String fileName, String gender, String nationality, Boolean favoriteYn) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.fileName = fileName;
        this.gender = gender;
        this.nationality = nationality;
        this.favoriteYn = favoriteYn;
     }
}

