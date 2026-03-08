package com.denodo.platform.api.post.controller;

import com.denodo.platform.api.post.dto.PostInfoDto;
import com.denodo.platform.api.post.entity.PostEntity;
import com.denodo.platform.api.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor // 생성자 주입 자동화
public class PostController {

    private final PostService postService;

    /**
     * 게시글 목록 조회 (검색 및 페이징)
     */
    @GetMapping
    public Map<String, Object> getPostList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Service에서 페이징 처리된 결과를 받아옴
        return postService.getPostList(keyword, page, size);
    }

    /**
     * 게시글 상세 조회 (누락되었던 메서드 추가)
     */
    @GetMapping("/{id}")
    public PostInfoDto getPost(@PathVariable("id") Long id) {
        // JPA 엔티티 기반으로 상세 데이터와 파일 정보를 반환
        return postService.getPost(id);
    }

    /**
     * 게시글 생성
     */
    @PostMapping
    public Map<String, Object> createPost(@RequestBody Map<String, Object> params) {
        postService.createPost(params);
        return Map.of("result", "OK");
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{id}")
    public Map<String, Object> modifyPost(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> params) {

        postService.modifyPost(id, params);
        return Map.of("result", "OK");
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> removePost(@PathVariable("id") Long id) {
        postService.removePost(id);
        return Map.of("result", "OK");
    }
}