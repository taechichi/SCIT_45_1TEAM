package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hospital")
public class HospitalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "hospital_name", nullable = false, length = 100)  // 최대 길이 설정
    private String hospitalName;

    @Column(name = "longitude", nullable = false, length = 50)
    private String longitude;

    @Column(name = "latitude", nullable = false, length = 50)
    private String latitude;

    @Column(name = "lang_cd", nullable = false, length = 10)
    private String langCd;

    @Column(name = "business_hours", length = 100)
    private String businessHours;
}
