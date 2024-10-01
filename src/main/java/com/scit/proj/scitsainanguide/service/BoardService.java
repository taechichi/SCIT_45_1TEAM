package com.scit.proj.scitsainanguide.service;

import com.scit.proj.scitsainanguide.domain.dto.BoardPictureDTO;
import com.scit.proj.scitsainanguide.domain.dto.MarkerBoardDTO;
import com.scit.proj.scitsainanguide.domain.entity.*;
import com.scit.proj.scitsainanguide.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

        log.debug("받은 파일의 수:{}",files.length);
        MemberEntity memberEntity = memberJpaRepository.findById(boardDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원아이디가 없습니다."));


        String placeId = boardDTO.getPlaceId();
        MarkerBoardEntity markerBoardEntity = null;
        Optional<HospitalEntity> hospitalEntityOptional = hospitalRepository.findById(placeId);
        Optional<ShelterEntity> shelterEntityOptional= Optional.empty();


        if(hospitalEntityOptional.isPresent()) {
            HospitalEntity hospitalEntity = hospitalEntityOptional.get();
            markerBoardEntity = MarkerBoardEntity.builder()
                    .memberId(memberEntity.getMemberId())
                    .hospital(hospitalEntity)
                    .contents(boardDTO.getContents())
                    .deleteYn(false)
                    .build();
        }else{
            shelterEntityOptional = shelterRepository.findById(Integer.valueOf(placeId));
            if(shelterEntityOptional.isPresent()){
                ShelterEntity shelterEntity = shelterEntityOptional.get();
                markerBoardEntity = MarkerBoardEntity.builder()
                        .memberId(memberEntity.getMemberId())
                        .shelter(shelterEntity)
                        .contents(boardDTO.getContents())
                        .deleteYn(false)
                        .build();
            }else{
                log.debug("일치하는 ID가 없습니다.");
            }
        }
        log.debug("저장되는 엔티티 : {}", markerBoardEntity);
        boardJPARepository.save(markerBoardEntity);

        // 파일 처리 및 저장
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                log.debug("처리 중인 파일: {}", file.getOriginalFilename());
                String newFilename = fileManagerService.saveFile(file);  // 파일 저장 처리

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

}
