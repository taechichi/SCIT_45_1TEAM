package com.scit.proj.scitsainanguide.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FileManagerService {

    private final String uploadDir = "C:/uploads";

    // 파일 저장
    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();

        // 파일 확장자 추출
        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < originalFilename.length() - 1) {
            fileExtension = originalFilename.substring(lastDotIndex);  // 확장자 추출 (.jpg, .png 등)
        }

        // UUID로 파일명 생성 및 확장자 추가
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // 파일을 저장할 디렉토리가 존재하지 않으면 생성
        Path directoryPath = Paths.get(uploadDir);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // 파일을 서버에 저장
        Path filePath = Paths.get(uploadDir, newFilename);
        Files.copy(file.getInputStream(), filePath);

        return newFilename;  // 저장된 파일명 반환
    }

    // 파일 삭제
    public boolean deleteFile(String filename) {
        Path filePath = Paths.get(uploadDir, filename);
        try {
            return Files.deleteIfExists(filePath); // 파일이 존재하면 삭제
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
