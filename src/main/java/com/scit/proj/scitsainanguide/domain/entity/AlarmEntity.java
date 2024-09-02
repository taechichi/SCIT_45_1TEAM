package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "alarm")
@Data
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id", nullable = false)
    private Integer alarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AlarmCategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "contents", length = 100, nullable = false)
    private String contents;

    @Column(name = "alarm_dt", nullable = false)
    private LocalDateTime alarmDt;

    @Column(name = "read_yn", nullable = false)
    private Boolean readYn = false;
}
