package com.denodo.platform.api.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db/test")
    public Map<String, Object> test() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);

        System.out.println("DB 연결 성공!");

        return Map.of(
                "select1", result,
                "version", version
        );
    }
}