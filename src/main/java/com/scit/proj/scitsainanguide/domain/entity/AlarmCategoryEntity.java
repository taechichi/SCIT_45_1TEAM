package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarm_category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "category_name", length = 10, nullable = false)
    private String categoryName;

    @Column(name = "category_name_ja", length = 10, nullable = false)
    private String categoryNameJa;
}
