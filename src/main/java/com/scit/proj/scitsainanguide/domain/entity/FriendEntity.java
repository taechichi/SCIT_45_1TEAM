package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend_member")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id", nullable = false)
    private Integer relation_id;

    @Column(name = "member_id", length = 50, nullable = false)
    private String memberId;

    @Column(name = "friend_id", length = 50, nullable = false)
    private String friendId;

    @Column(name = "friend_yn", nullable = false)
    private Boolean friendYn;

    @Column(name = "favorite_yn", nullable = false)
    private Boolean favoriteYn;

    @Column(name = "request_dt")
    private LocalDateTime requestDt;

    @Column(name = "accept_dt")
    private LocalDateTime acceptDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", insertable = false, updatable = false)
    private MemberEntity friend;
}
