package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.BoardPictureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardPictureRepository extends JpaRepository<BoardPictureEntity,Integer> {
    List<BoardPictureEntity> findByBoard_BoardId(Integer boardId);
}
