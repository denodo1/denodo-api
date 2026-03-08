package com.denodo.platform.api.file.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface FileMapper {
    void insertFile(Map<String, Object> param);
}
