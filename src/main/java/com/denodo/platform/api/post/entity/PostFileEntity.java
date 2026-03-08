package com.denodo.platform.api.post.entity;

import com.denodo.platform.api.file.entity.FileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TD_POST_FILE", schema = "denodo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_FILE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID", nullable = false)
    private FileEntity file;

    @Column(name = "CREATE_DATE", nullable = false, updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate;

    @Builder
    public PostFileEntity(PostEntity post, FileEntity file) {
        this.post = post;
        this.file = file;
    }
}