package com.scit.proj.scitsainanguide.repository;

import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, String> {
}
