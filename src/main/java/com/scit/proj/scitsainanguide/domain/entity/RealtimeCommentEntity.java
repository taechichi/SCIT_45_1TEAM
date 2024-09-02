package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "realtime_comment")
public class RealtimeCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_num")
    private Integer commentNum;

    @ManyToOne
    @JoinColumn(name = "reply_num")
    private RealtimeCommentEntity replyRealtimeComment; // 자기 참조 관계

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Column(name = "contents", nullable = false, length = 200)
    private String contents;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;
}
