package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hospital")
public class HospitalEntity {

    @Id
    @Column(name = "hospital_id", nullable = false, length = 500)
    private String hospitalId;

    @Column(name = "hospital_name", nullable = false, length = 200)
    private String hospitalName;

    @Column(name = "longitude", nullable = false, length = 20)
    private String longitude;

    @Column(name = "latitude", nullable = false, length = 20)
    private String latitude;

    @Column(name = "lang_cd", nullable = false, length = 2)
    private String langCd;

    @Column(name = "business_hours", length = 30)
    private String businessHours;
}
