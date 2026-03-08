package com.denodo.platform.api.post.service;

import com.denodo.platform.api.post.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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
    public Map<String, Object> getPost(Long postId) {
        Map<String, Object> post = mapper.selectPostById(postId);
        List<Map<String, Object>> files = mapper.selectFilesByPostId(postId);
        post.put("files", files);
        return post;
    }

    @Transactional
    public Map<String, Object> createPost(Map<String, Object> params) {
        mapper.insertPost(params);
        Long postId = ((Number) params.get("postId")).longValue();

        List<Number> fileIdList = (List<Number>) params.get("fileIdList");
        if (fileIdList != null) {
            for (Number n : fileIdList) {
                Map<String, Object> postFileMap = new HashMap<>();
                postFileMap.put("postId", postId);
                postFileMap.put("fileId", n.longValue());
                mapper.insertPostFile(postFileMap);
            }
        }

        return Map.of("postId", postId);
    }

    @Transactional
    public Map<String, Object> modifyPost(Long postId, Map<String, Object> params) {
        params.put("postId", postId);

        int cnt = mapper.updatePost(params);
        if (cnt == 0) {
            throw new IllegalArgumentException("수정 실패: " + postId);
        }

        // 1) fileIdList가 "키 자체가 존재"하면 -> 첨부를 교체한다고 해석
        // 2) 키가 아예 없으면 -> 첨부는 건드리지 않는다고 해석(선택)
        if (params.containsKey("fileIdList")) {

            // 기존 매핑 전부 삭제
            mapper.deletePostFilesByPostId(postId);

            // 새 매핑 등록
            @SuppressWarnings("unchecked")
            List<?> fileIdList = (List<?>) params.get("fileIdList");

            if (fileIdList != null) {
                for (Object n : fileIdList) {
                    if (n == null) continue;

                    long fileId = (n instanceof Number)
                            ? ((Number) n).longValue()
                            : Long.parseLong(String.valueOf(n));

                    Map<String, Object> postFileMap = new HashMap<>();
                    postFileMap.put("postId", postId);
                    postFileMap.put("fileId", fileId);
                    mapper.insertPostFile(postFileMap);
                }
            }
        }

        return Map.of("postId", postId);
    }

    @Transactional
    public void removePost(long postId) {
        int cnt = mapper.deletePost(postId);
        if (cnt == 0) {
            throw new IllegalArgumentException("삭제 실패: " + postId);
        }
    }
}
