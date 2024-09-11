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
public class FriendDTO {

    private Integer relationId;
    private String memberId;
    private String friendId;
    private Boolean friendYn;
    private Boolean favoriteYn;
    private LocalDateTime requestDt;
    private LocalDateTime acceptDt;

    // 내 친구 목록 출력에 필요한 필드
    private String nickname;
    private String nationality;

    // 내 친구 목록 출력에 필요한 DTO 생성자
    public FriendDTO(Integer relationId, String friendId, String nickname, boolean favoriteYn, String nationality) {
        this.relationId = relationId;
        this.friendId = friendId;
        this.nickname = nickname;
        this.favoriteYn = favoriteYn;
        this.nationality = nationality;
    }

    // 내 친구 신청 목록 출력에 필요한 DTO 생성자
    public FriendDTO(String friendId, String nickname, String nationality, LocalDateTime requestDt) {
        this.friendId = friendId;
        this.nickname = nickname;
        this.nationality = nationality;
        this.requestDt = requestDt;
    }
}
