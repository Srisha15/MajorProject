package com.E2DShare.filemngt.repository;

import com.E2DShare.filemngt.entity.FileShareMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileShareMetaRepository extends JpaRepository<FileShareMetadata, String>
{
    List<FileShareMetadata> findByReceiverId(String receiverId);



}
