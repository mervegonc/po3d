package com.project.po3d.repository;

import com.project.po3d.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    void deleteById(UUID id);


}
