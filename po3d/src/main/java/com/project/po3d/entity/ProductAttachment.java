package com.project.po3d.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "product_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String fileUrl; // Resim veya video dosya yolu

    @Column(nullable = false, length = 50)
    private String fileType; // "image" veya "video" olabilir

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Timestamp uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = new Timestamp(System.currentTimeMillis());
    }
}
