package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.domain.entity.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StatusJpaRepository extends JpaRepository<StatusEntity, Integer> {

}
