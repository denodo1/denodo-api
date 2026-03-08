package com.denodo.platform.api.file.repository;

import com.denodo.platform.api.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    // 기본 CRUD 메서드(save, findById 등)가 자동 생성됩니다.
}