package com.scit.proj.scitsainanguide.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_picture")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardPictureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private MarkerBoardEntity board;

    @Column(name = "guidebook_id")
    private Integer guidebookId;

    @Column(name = "path")
    private String path;

    @Column(name = "ori_filename", nullable = false)
    private String oriFilename;

    @Column(name = "new_filename", nullable = false)
    private String newFilename;

}
