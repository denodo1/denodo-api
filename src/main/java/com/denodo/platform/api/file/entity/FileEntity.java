package com.denodo.platform.api.file.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TD_FILE", schema = "denodo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long id;

    @Column(name = "ORIGIN_NAME", nullable = false, length = 255)
    private String originName;

    @Column(name = "SAVE_NAME", nullable = false, length = 255)
    private String saveName;

    @Column(name = "SAVE_PATH", nullable = false, length = 500)
    private String savePath;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Column(name = "CONTENT_TYPE", length = 100)
    private String contentType;

    @Column(name = "CREATE_DATE", nullable = false, updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate;

    @Builder
    public FileEntity(String originName, String saveName, String savePath, Long fileSize, String contentType) {
        this.originName = originName;
        this.saveName = saveName;
        this.savePath = savePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }
}