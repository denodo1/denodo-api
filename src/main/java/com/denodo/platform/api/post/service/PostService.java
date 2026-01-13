package com.denodo.platform.api.post.service;

import com.denodo.platform.api.post.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final PostMapper mapper;

    public PostService(PostMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPostList(String keyword, int page, int size) {
        int offset = page * size;
        return mapper.selectPostList(keyword, offset, size);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPost(long postId) {
        Map<String, Object> post = mapper.selectPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시글 없음: " + postId);
        }
        return post;
    }

    @Transactional
    public void createPost(Map<String, Object> params) {
        int cnt = mapper.insertPost(params);
        if (cnt == 0) {
            throw new IllegalArgumentException("등록 실패: ");
        }
    }

    @Transactional
    public void modifyPost(Long postId, Map<String, Object> params) {
        params.put("postId", postId);
        int cnt = mapper.updatePost(params);
        if (cnt == 0) {
            throw new IllegalArgumentException("수정 실패: " + postId.toString());
        }
    }

    @Transactional
    public void removePost(long postId) {
        int cnt = mapper.deletePost(postId);
        if (cnt == 0) {
            throw new IllegalArgumentException("삭제 실패: " + postId);
        }
    }
}
