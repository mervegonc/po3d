package com.project.po3d.repository;

import com.project.po3d.entity.ProductAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductAttachmentRepository extends JpaRepository<ProductAttachment, UUID> {
    List<ProductAttachment> findByProductId(UUID productId);
    void deleteByProductId(UUID productId);
}
