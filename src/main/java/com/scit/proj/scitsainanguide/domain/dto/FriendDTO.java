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

    private String memberId;
    private String friendId;
    private Boolean friendYn;
    private Boolean favoriteYn;
    private LocalDateTime requestDt;
    private LocalDateTime acceptDt;
}
