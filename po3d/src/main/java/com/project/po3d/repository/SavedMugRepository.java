package com.project.po3d.repository;


import com.project.po3d.entity.SavedMug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedMugRepository extends JpaRepository<SavedMug, Long> {
    // Gerekirse özel sorgular ekleyebilirsiniz
}
