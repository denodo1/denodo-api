package com.denodo.platform.api.post.dto;

import com.denodo.platform.api.file.entity.FileEntity;
import com.denodo.platform.api.post.entity.PostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PostInfoDto {
    private Long postId;
    private String postTitle;
    private String postContent;
    private String postAuthor;
    private LocalDateTime createDate;
    private List<FileDto> files; // 파일 정보를 담을 별도 DTO 리스트

    public PostInfoDto(PostEntity entity) {
        this.postId = entity.getId();
        this.postTitle = entity.getTitle();
        this.postContent = entity.getContent();
        this.postAuthor = entity.getAuthor();
        this.createDate = entity.getCreateDate();

        // PostFileEntity -> FileDto 변환
        this.files = entity.getPostFiles().stream()
                .map(pf -> new FileDto(pf.getFile()))
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    public static class FileDto {
        private Long fileId;
        private String originName;
        private String savePath;

        public FileDto(FileEntity file) {
            this.fileId = file.getId();
            this.originName = file.getOriginName();
            this.savePath = file.getSavePath();
        }
    }
}