package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "shelter")
@Entity
public class ShelterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelter_id")
    private Integer shelterId;

    @Column(name = "shelter_name" , nullable = false)
    private String shelterName;

    @Column(name = "latitude" , nullable = false)
    private String latitude;

    @Column(name = "longitude" , nullable = false)
    private String longitude;

    @Column(name = "flood_yn")
    @Builder.Default
    private boolean floodYn = false;

    @Column(name = "earthquake_yn")
    @Builder.Default
    private boolean earthquakeYn = false;

    @Column(name = "earth_yn")
    @Builder.Default
    private boolean earthYn = false;

    @Column(name = "hightide_yn")
    @Builder.Default
    private boolean highTideYn = false;

    @Column(name = "inland_flooding_yn")
    @Builder.Default
    private boolean inlandFloodingYn = false;

    @Column(name = "tsunami_yn")
    @Builder.Default
    private boolean tsunamiYn  = false;

    @Column(name = "fire_yn")
    @Builder.Default
    private boolean fireYn  = false;

    @Column(name = "volcano_yn")
    @Builder.Default
    private boolean volcanoYn = false;

    @Column(name = "lang_cd", nullable = false)
    @Builder.Default
    private String lang_cd = "ja";
}
