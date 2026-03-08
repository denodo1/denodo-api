package com.denodo.platform.api.post.dto;

import com.denodo.platform.api.post.entity.PostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostSrchDto {
    private Long postId;
    private String postTitle;
    private String postAuthor;
    private String delYn;
    private LocalDateTime createDate;

    // Entity -> DTO 변환 생성자
    public PostSrchDto(PostEntity entity) {
        this.postId = entity.getId();
        this.postTitle = entity.getTitle();
        this.postAuthor = entity.getAuthor();
        this.delYn = entity.getDelYn();
        this.createDate = entity.getCreateDate();
    }
}