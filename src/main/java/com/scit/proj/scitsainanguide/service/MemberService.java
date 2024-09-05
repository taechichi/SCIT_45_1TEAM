package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.MemberDTO;
import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.domain.entity.StatusEntity;
import com.scit.proj.scitsainanguide.repository.MemberJpaRepository;
import com.scit.proj.scitsainanguide.repository.StatusJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final StatusJpaRepository statusJpaRepository;

    //암호화 인코더
    private final BCryptPasswordEncoder passwordEncoder;

    private final String uploadDir = "C:/profile-photos";

    public MemberDTO registerMember(MemberDTO memberDTO, MultipartFile file) throws IOException {
        // 파일이 있을 경우 처리
        if (!file.isEmpty()) {
            // 업로드 디렉토리 생성
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // 고유한 파일명 생성
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 파일의 실제 경로 생성
            Path filePath = Paths.get(uploadPath.getAbsolutePath(), uniqueFileName);

            // 파일 저장
            file.transferTo(filePath.toFile());

            // 파일명(경로) DTO에 설정
            memberDTO.setFileName(uniqueFileName);
        }

        // statusId을 1로 설정
        memberDTO.setStatusId(1);

        // StatusEntity 객체 조회
        StatusEntity statusEntity = statusJpaRepository.findById(memberDTO.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 status가 없습니다."));

        // MemberEntity 객체 생성 및 저장
        MemberEntity entity = createMemberEntity(memberDTO, statusEntity);
        memberJpaRepository.save(entity);

        return memberDTO;
    }

    // MemberEntity 객체 생성 메서드
    private MemberEntity createMemberEntity(MemberDTO memberDTO, StatusEntity statusEntity) {
        return MemberEntity.builder()
                .memberId(memberDTO.getMemberId())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .nickname(memberDTO.getNickname())
                .email(memberDTO.getEmail())
                .gender(memberDTO.getGender())
                .phone(memberDTO.getPhone())
                .nationality(memberDTO.getNationality())
                .adminYn(false)  // 기본값
                .withdraw(false) // 기본값
                .fileName(memberDTO.getFileName()) // DTO에서 파일 이름 설정
                .status(statusEntity) // StatusEntity 설정
                .build();
    }

    public boolean findId(String searchId) {
        return memberJpaRepository.findById(searchId).isEmpty();
    }

    public boolean check(String memberId) {
        return memberJpaRepository.findById(memberId).isPresent();
    }

    public MemberDTO findByMemberId(String username) {

        MemberEntity entity = memberJpaRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException(username + " : 아이디가 없습니다."));
        MemberDTO dto = MemberDTO.builder()
                .memberId(entity.getMemberId())  // 게터 메서드를 사용
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .gender(entity.getGender())
                .phone(entity.getPhone())
                .nationality(entity.getNationality())
                .adminYn(entity.getAdminYn())
                .withdraw(entity.getWithdraw())
                .lastLoginDt(entity.getLastLoginDt())
                .lastStUpdateDt(entity.getLastStUpdateDt())
                .fileName(entity.getFileName())
                .stMessage(entity.getStMessage())
                .endTime(entity.getEndTime())
                .statusId(entity.getStatus() != null ? entity.getStatus().getStatusId() : null) // StatusEntity의 ID를 가져옴
                .build();
        return dto;
    }


    /**
     * 첨부파일 다운로드
     * @param memberId 다운로드 할 글번호
     * @param uploadPath 첨부파일의 경로
     * @param response 첨부파일을 보낼 스트림
     */
    public void download(String memberId, String uploadPath, HttpServletResponse response) throws IOException {
        // 전달된 글 번호로 파일명 확인
        MemberEntity memberEntity = memberJpaRepository.findById(memberId)
                // 보드엔티티는 모든걸 알고있다.
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
        //response의 헤더에 원래의 파일명을 셋팅 // 파일명(orugunalName을 한글일때 UTF-8로 인코딩
        response.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode(memberEntity.getFileName(), StandardCharsets.UTF_8));
        //저장된 파일 경로
        String fullPath = uploadPath + "/" + memberEntity.getFileName();

        //서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력 스트림
        FileInputStream filein = null;
        ServletOutputStream fileout = null;

        filein = new FileInputStream(fullPath); //서버의 파일과 프로그램
        fileout = response.getOutputStream();	//프로그램과 클라이언트

        //Spring의 파일 관련 유틸 이용하여 출력
        FileCopyUtils.copy(filein, fileout);

        filein.close();
        fileout.close();
    }
}

