package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Integer messageId;

    @Column(name = "member_id", length = 30, nullable = false)
    private String memberId;

    @Column(name = "contents", length = 500, nullable = false)
    private String contents;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private MemberEntity member;
}
