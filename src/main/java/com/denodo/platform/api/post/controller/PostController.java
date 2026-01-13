package com.denodo.platform.api.post.controller;

import com.denodo.platform.api.post.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Map<String, Object> getPostList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<Map<String, Object>> postList = postService.getPostList(keyword, page, size);
        long total = postService.getPostList(keyword, 0, Integer.MAX_VALUE).size();

        return Map.of(
                "page", page,
                "size", size,
                "total", total,
                "postList", postList
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> getPost(@PathVariable("id") long id) {
        return postService.getPost(id);
    }

    @PostMapping
    public Map<String, Object> createPost(@RequestBody Map<String, Object> params) {
        postService.createPost(params);
        return Map.of("result", "OK");
    }

    @PutMapping("/{id}")
    public Map<String, Object> modifyPost(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params
    ) {
        postService.modifyPost(id, params);
        return Map.of("result", "OK");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> removePost(@PathVariable("id") long id) {
        postService.removePost(id);
        return Map.of("result", "OK");
    }
}
