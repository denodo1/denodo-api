package com.denodo.platform.api.file.service;

import com.denodo.platform.api.file.mapper.FileMapper;
import java.nio.file.attribute.PosixFilePermissions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper fileMapper;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.serve-static:false}")
    private boolean serveStatic;

    public Map<String, Object> upload(MultipartFile file) {

        String originName = file.getOriginalFilename();
        String ext = originName.substring(originName.lastIndexOf("."));
        String saveName = UUID.randomUUID() + ext;

        String subDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path dirPath = Paths.get(uploadDir, subDir);

        try {
            Files.createDirectories(dirPath);

            Path savePath = dirPath.resolve(saveName);
            file.transferTo(savePath);

            if (!serveStatic) {
                Files.setPosixFilePermissions(savePath, PosixFilePermissions.fromString("rwxr-xr-x")); // 755
            }

            Map<String, Object> param = new HashMap<>();
            param.put("originName", originName);
            param.put("saveName", saveName);
            param.put("savePath", savePath.toString());
            param.put("fileSize", file.getSize());
            param.put("contentType", file.getContentType());

            fileMapper.insertFile(param);

            Long fileId = ((Number) param.get("fileId")).longValue();

            return Map.of(
                    "fileId", fileId,
                    "url", "/uploads/" + subDir + "/" + saveName
            );

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
