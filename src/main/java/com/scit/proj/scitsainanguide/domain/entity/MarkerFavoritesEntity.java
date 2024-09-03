package com.scit.proj.scitsainanguide.domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "marker_favorites")
public class MarkerFavoritesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id")
    private HospitalEntity hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id", referencedColumnName = "shelter_id")
    private ShelterEntity shelter;

    @Column(name = "nickname", length = 20)
    private String nickname;
}
