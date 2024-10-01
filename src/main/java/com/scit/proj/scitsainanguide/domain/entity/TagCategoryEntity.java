package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tag_category")
public class TagCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "tag_content", nullable = false, length = 30)
    private String tag_content;

    @Column(name = "tag_content_ja", nullable = false, length = 30)
    private String tag_content_ja;

}
