package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "status_id", nullable=false)
    private Integer statusId;

    @Column(name="status_name", nullable = false, length = 10)
    private String statusName;

    @Column(name =  "lang_cd", nullable = false, length = 2)
    private String langCd;
}
