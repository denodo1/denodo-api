package com.denodo.platform.api.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    List<Map<String, Object>> selectPostList(
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    Map<String, Object> selectPostById(@Param("postId") long postId);

    int insertPost(Map<String, Object> post);

    int updatePost(Map<String, Object> post);

    int deletePost(@Param("postId") long postId);
}
