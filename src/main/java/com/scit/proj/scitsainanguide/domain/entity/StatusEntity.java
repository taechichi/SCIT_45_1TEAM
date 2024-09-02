package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "status")
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id", nullable=false)
    private Integer statusId;

    @Column(name="status_name", nullable = false, length = 10)
    private String statusName;

    @Column(name =  "status_name_ja", nullable = false, length = 10)
    private String statusNameJa;
}
