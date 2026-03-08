package com.denodo.platform.api.file.service;

import java.nio.file.attribute.PosixFilePermissions;

import com.denodo.platform.api.file.entity.FileEntity;
import com.denodo.platform.api.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository; // FileMapper 대신 주입

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.serve-static:false}")
    private boolean serveStatic;

    @Transactional // DB 저장이 포함되므로 트랜잭션 추가
    public Map<String, Object> upload(MultipartFile file) {
        String originName = file.getOriginalFilename();
        String ext = originName.substring(originName.lastIndexOf("."));
        String saveName = UUID.randomUUID().toString() + ext;

        String subDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path dirPath = Paths.get(uploadDir, subDir);

        try {
            Files.createDirectories(dirPath);
            Path savePath = dirPath.resolve(saveName);
            file.transferTo(savePath);

            if (!serveStatic) {
                Files.setPosixFilePermissions(savePath, PosixFilePermissions.fromString("rwxr-xr-x"));
            }

            // [변경 포인트] Map 대신 Entity 객체 생성
            FileEntity fileEntity = FileEntity.builder()
                    .originName(originName)
                    .saveName(saveName)
                    .savePath(savePath.toString())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build();

            // [변경 포인트] fileMapper.insertFile(param) -> repository.save(entity)
            // save() 호출 후 리턴된 객체에서 생성된 ID를 바로 가져올 수 있습니다.
            FileEntity savedFile = fileRepository.save(fileEntity);

            return Map.of(
                    "fileId", savedFile.getId(),
                    "url", "/uploads/" + subDir + "/" + saveName
            );

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
