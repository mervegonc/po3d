package com.project.po3d.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.po3d.entity.PurchasedMug;

@Repository
public interface ModelRepository extends JpaRepository<PurchasedMug, Long> {
    // Gerekirse özel sorgular ekleyebilirsiniz
}

