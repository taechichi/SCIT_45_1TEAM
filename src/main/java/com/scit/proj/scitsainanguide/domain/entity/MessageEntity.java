package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Integer messageId;

    @Column(name = "sender_id", length = 30, nullable = false)
    private String senderId;

    @Column(name = "receiver_id", length = 30, nullable = false)
    private String receiverId;

    @Column(name = "content", length = 500, nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private MemberEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private MemberEntity receiver;

    @Column(name = "read_dt")
    private LocalDateTime readDt;
}
