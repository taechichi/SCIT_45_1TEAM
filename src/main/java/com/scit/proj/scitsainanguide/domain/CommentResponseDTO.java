package com.scit.proj.scitsainanguide.domain;


import com.scit.proj.scitsainanguide.domain.dto.RealtimeCommentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {
    private LocalDateTime serverNow;
    private List<RealtimeCommentDTO> comments;
}
