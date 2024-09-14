package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, String> {
    
    // JPA 자동 생성 메서드 활용
    List<MemberEntity> findByEndTimeBefore(LocalDateTime now);

}
