package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.domain.entity.StatusEntity;
import com.scit.proj.scitsainanguide.repository.MemberJpaRepository;
import com.scit.proj.scitsainanguide.repository.StatusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final StatusJpaRepository statusJpaRepository;

    // private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registerMember(String memberId, String password, String nickname, String email, String gender, String phone, String nationality, String fileName, Integer statusId) {
        // StatusEntity 객체 조회
        StatusEntity statusEntity = statusJpaRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));

        // 비밀번호 암호화
        // String encodedPassword = passwordEncoder.encode(password);

        // MemberEntity 객체를 Builder 패턴을 사용하여 생성
        MemberEntity member = MemberEntity.builder()
                .memberId(memberId)
                .password(password)
                .nickname(nickname)
                .email(email)
                .gender(gender)
                .phone(phone)
                .nationality(nationality)
                .adminYn(false)  // 기본값
                .withdraw(false) // 기본값
                .fileName(fileName)
                .status(statusEntity)  // StatusEntity 객체 설정
                .build();

        // 엔티티 저장
        memberJpaRepository.save(member);
    }

    public boolean findId(String searchId) {
        // 주어진 searchId가 존재하지 않으면 true를 반환
        return !memberJpaRepository.findById(searchId).isPresent();
    }

    public boolean check(String memberId) {
        // 주어진 memberId가 존재하면 true를 반환
        return memberJpaRepository.findById(memberId).isPresent();
    }
}
