package com.denodo.platform.api.post.service;

import com.denodo.platform.api.file.entity.FileEntity;
import com.denodo.platform.api.post.dto.PostInfoDto;
import com.denodo.platform.api.post.dto.PostSrchDto;
import com.denodo.platform.api.post.entity.PostEntity;
import com.denodo.platform.api.post.entity.PostFileEntity;
import com.denodo.platform.api.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final jakarta.persistence.EntityManager em; // Proxy 객체 생성을 위한 em 주입

    /**
     * 게시글 목록 조회
     */
    public Map<String, Object> getPostList(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        Page<PostEntity> result;

        if (keyword != null && !keyword.trim().isEmpty()) {
            result = postRepository.findByTitleContainingAndDelYn(keyword, "N", pageRequest);
        } else {
            result = postRepository.findByDelYn("N", pageRequest);
        }

        List<PostSrchDto> postList = result.getContent().stream()
                .map(PostSrchDto::new)
                .collect(Collectors.toList());

        return Map.of(
                "total", result.getTotalElements(),
                "postList", postList
        );
    }

    /**
     * 게시글 상세 조회
     */
    public PostInfoDto getPost(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. (ID: " + id + ")"));

        return new PostInfoDto(post);
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public Map<String, Object> createPost(Map<String, Object> params) {
        // 1. Post 엔티티 생성
        PostEntity post = PostEntity.builder()
                .title((String) params.get("postTitle"))
                .content((String) params.get("postContent"))
                .author((String) params.get("postAuthor"))
                .build();

        // 2. 파일 리스트 처리 (ClassCastException 방지 로직)
        Object fileIdsObj = params.get("fileIdList");
        if (fileIdsObj instanceof List<?> rawList) {
            for (Object obj : rawList) {
                // Integer, Long, String 등 어떤 형식이든 숫자로 안전하게 변환
                Long fileId = Long.valueOf(String.valueOf(obj));

                FileEntity fileProxy = em.getReference(FileEntity.class, fileId);

                PostFileEntity postFile = PostFileEntity.builder()
                        .post(post)
                        .file(fileProxy)
                        .build();

                post.getPostFiles().add(postFile);
            }
        }

        // 3. 저장
        PostEntity savedPost = postRepository.save(post);
        return Map.of("postId", savedPost.getId());
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Map<String, Object> modifyPost(Long postId, Map<String, Object> params) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + postId));

        post.update((String) params.get("postTitle"), (String) params.get("postContent"));

        // 파일 리스트 수정 (ClassCastException 방지 로직 동일 적용)
        if (params.containsKey("fileIdList")) {
            post.getPostFiles().clear();

            Object fileIdsObj = params.get("fileIdList");
            if (fileIdsObj instanceof List<?> rawList) {
                for (Object obj : rawList) {
                    Long fileId = Long.valueOf(String.valueOf(obj));
                    FileEntity fileProxy = em.getReference(FileEntity.class, fileId);

                    PostFileEntity postFile = PostFileEntity.builder()
                            .post(post)
                            .file(fileProxy)
                            .build();

                    post.getPostFiles().add(postFile);
                }
            }
        }

        return Map.of("postId", post.getId());
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @Transactional
    public void removePost(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("삭제 실패: " + postId));

        // 실제 삭제 대신 상태값 변경 (DDL의 POST_DEL_YN 반영)
        post.markAsDeleted();
    }
}
