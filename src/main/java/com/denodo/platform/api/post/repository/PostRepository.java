package com.denodo.platform.api.post.repository;

import com.denodo.platform.api.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // 키워드 없이 전체 목록 조회 시 사용 (추가)
    Page<PostEntity> findByDelYn(String delYn, Pageable pageable);

    // 제목 키워드 검색 + 페이징 (MyBatis의 selectPostList 대체)
    Page<PostEntity> findByTitleContainingAndDelYn(String title, String delYn, Pageable pageable);
}