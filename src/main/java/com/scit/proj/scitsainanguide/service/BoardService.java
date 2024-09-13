package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.entity.HospitalEntity;
import com.scit.proj.scitsainanguide.domain.entity.MarkerBoardEntity;
import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.domain.entity.ShelterEntity;
import com.scit.proj.scitsainanguide.repository.BoardJPARepository;
import com.scit.proj.scitsainanguide.repository.HospitalRepository;
import com.scit.proj.scitsainanguide.repository.MemberJpaRepository;
import com.scit.proj.scitsainanguide.repository.ShelterRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardJPARepository boardJPARepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ShelterRepository shelterRepository;
    private final HospitalRepository hospitalRepository;

    public void write(MarkerBoardDTO boardDTO) throws IOException {
        MemberEntity memberEntity = memberJpaRepository.findById(boardDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원아이디가 없습니다."));


        // shelterName으로 ShelterEntity 조회
        HospitalEntity hospitalEntity = hospitalRepository.findById(boardDTO.getHospitalName())
                .orElseThrow(() -> new EntityNotFoundException("병원이 없습니다."));

        MarkerBoardEntity markerBoardEntity = MarkerBoardEntity.builder()
                .memberId(memberEntity.getMemberId())
                .hospital(hospitalEntity)
                .contents(boardDTO.getContents())
                .deleteYn(false)
                .build();


        log.debug("저장되는 엔티티 : {}", markerBoardEntity);
        boardJPARepository.save(markerBoardEntity);
    }
}
