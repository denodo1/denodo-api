package com.denodo.platform.api.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TD_POST", schema = "denodo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @Column(name = "POST_TITLE", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "POST_CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "POST_AUTHOR", nullable = false, length = 50)
    private String author;

    @Column(name = "POST_DEL_YN", nullable = false, length = 1)
    private String delYn = "N";

    @Column(name = "CREATE_DATE", nullable = false, updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate;

    @Column(name = "UPDATE_DATE", nullable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateDate;

    // 게시글 삭제 시 관련 파일 매핑 정보도 함께 삭제되도록 설정 (파일 원본 삭제는 Service 로직에서 처리)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFileEntity> postFiles = new ArrayList<>();

    @Builder
    public PostEntity(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void markAsDeleted() {
        this.delYn = "Y";
    }
}