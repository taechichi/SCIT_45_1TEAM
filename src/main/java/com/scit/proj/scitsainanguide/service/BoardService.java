package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.BoardPictureDTO;
import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.entity.*;
import com.scit.proj.scitsainanguide.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardJPARepository boardJPARepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ShelterRepository shelterRepository;
    private final HospitalRepository hospitalRepository;
    private final FileManagerService fileManagerService;
    private final BoardPictureRepository boardPictureRepository;

    public void write(MarkerBoardDTO boardDTO, MultipartFile[] files) throws IOException {
        MemberEntity memberEntity = memberJpaRepository.findById(boardDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원아이디가 없습니다."));

        String placeId = boardDTO.getPlaceId();
        MarkerBoardEntity markerBoardEntity;

        // boardId가 있을 경우 수정, 없을 경우 새로 생성
        if (boardDTO.getBoardId() != null) {
            // 수정인 경우 기존 엔티티를 가져와 업데이트
            markerBoardEntity = boardJPARepository.findById(boardDTO.getBoardId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));
            markerBoardEntity.setContents(boardDTO.getContents());
            markerBoardEntity.setDeleteYn(false);
        } else {
            // 새 글인 경우 새 엔티티 생성
            markerBoardEntity = new MarkerBoardEntity();
            markerBoardEntity.setMemberId(memberEntity.getMemberId());
            markerBoardEntity.setContents(boardDTO.getContents());
            markerBoardEntity.setDeleteYn(false);

            // 병원이나 대피소와 연결
            Optional<HospitalEntity> hospitalEntityOptional = hospitalRepository.findById(placeId);
            if (hospitalEntityOptional.isPresent()) {
                markerBoardEntity.setHospital(hospitalEntityOptional.get());
            } else {
                Optional<ShelterEntity> shelterEntityOptional = shelterRepository.findById(Integer.valueOf(placeId));
                shelterEntityOptional.ifPresent(markerBoardEntity::setShelter);
            }
        }

        // 게시글 저장
        boardJPARepository.save(markerBoardEntity);

        // 파일 처리 및 저장
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String newFilename = fileManagerService.saveFile(file);
                if (newFilename != null) {
                    BoardPictureEntity pictureEntity = BoardPictureEntity.builder()
                            .board(markerBoardEntity)
                            .path("/uploads/" + newFilename)
                            .oriFilename(file.getOriginalFilename())
                            .newFilename(newFilename)
                            .build();
                    boardPictureRepository.save(pictureEntity);
                }
            }
        }
    }


    public List<MarkerBoardDTO> findByPlaceIdWithPaging(String placeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 병원 ID로 검색
        Page<MarkerBoardEntity> boardPage = boardJPARepository.findByHospitalIdAndDeleteYnFalse(placeId, pageable);

        // 결과가 없으면 대피소 ID로 검색
        if (boardPage.isEmpty()) {
            try {
                boardPage = boardJPARepository.findByShelterIdAndDeleteYnFalse(Integer.valueOf(placeId), pageable);
            } catch (NumberFormatException e) {
                return new ArrayList<>(); // 빈 리스트 반환
            }
        }

        // 게시글 리스트를 DTO로 변환하고 반환
        List<MarkerBoardDTO> boardDTOList = new ArrayList<>();

        for (MarkerBoardEntity boardEntity : boardPage) {
            // 각 게시글의 boardId로 사진 검색
            List<BoardPictureEntity> pictureEntities = boardPictureRepository.findByBoard_BoardId(boardEntity.getBoardId());

            // 사진 정보를 DTO로 변환
            List<BoardPictureDTO> pictureDTOList = new ArrayList<>();
            for (BoardPictureEntity picture : pictureEntities) {
                BoardPictureDTO pictureDTO = BoardPictureDTO.builder()
                        .path(picture.getPath())
                        .oriFilename(picture.getOriFilename())
                        .newFilename(picture.getNewFilename())
                        .build();
                pictureDTOList.add(pictureDTO);
            }

            // MarkerBoardDTO 빌더를 사용해 게시글 정보와 이미지를 DTO에 추가
            MarkerBoardDTO boardDTO = MarkerBoardDTO.builder()
                    .memberId(boardEntity.getMemberId())
                    .boardId(boardEntity.getBoardId())
                    .contents(boardEntity.getContents())
                    .createDt(boardEntity.getCreateDt())
                    .pictures(pictureDTOList)
                    .build();

            boardDTOList.add(boardDTO);
        }

        return boardDTOList;
    }

    // 게시글 업데이트
    public void updateBoard(MarkerBoardDTO boardDTO) {
        // boardId로 엔티티를 조회
        MarkerBoardEntity markerBoardEntity = boardJPARepository.findById(boardDTO.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        markerBoardEntity.setDeleteYn(boardDTO.getDeleteYn());
        boardJPARepository.save(markerBoardEntity);  // 수정된 엔티티 저장
    }
    
    // 게시글 번호 검색
    public MarkerBoardDTO findById(Integer boardId) {
        MarkerBoardEntity markerBoardEntity = boardJPARepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        // Entity를 DTO로 변환 (필요한 필드만 추출)
        MarkerBoardDTO boardDTO = MarkerBoardDTO.builder()
                .boardId(markerBoardEntity.getBoardId())
                .contents(markerBoardEntity.getContents())
                .deleteYn(markerBoardEntity.getDeleteYn())
                .build();

        return boardDTO;
    }


}
