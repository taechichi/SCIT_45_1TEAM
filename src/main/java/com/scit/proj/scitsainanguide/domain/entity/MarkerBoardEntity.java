package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "marker_board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MarkerBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Integer boardId;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private HospitalEntity hospital;

    @Column(name = "contents", length = 500)
    private String contents;

    @CreatedDate
    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "delete_reason", length = 50)
    private String deleteReason;

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;
}
