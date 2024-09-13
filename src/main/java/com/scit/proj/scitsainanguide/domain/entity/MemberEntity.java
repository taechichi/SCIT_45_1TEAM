package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @Column(name = "member_id", nullable = false, length = 30)
    private String memberId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "nationality", nullable = false, length = 20)
    private String nationality;

    @Column(name = "admin_yn", nullable = false)
    private Boolean adminYn;

    @Column(name = "withdraw", nullable = false)
    private Boolean withdraw;

    @Column(name = "last_login_dt")
    private LocalDateTime lastLoginDt;

    @Column(name = "last_st_update_dt")
    private LocalDateTime lastStUpdateDt;

    @Column(name = "file_name", nullable = false, length = 50)
    private String fileName;

    @Column(name = "st_message", length = 50)
    private String stMessage;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private StatusEntity status;
}
