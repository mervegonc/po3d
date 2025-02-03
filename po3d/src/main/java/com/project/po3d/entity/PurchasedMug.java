package com.project.po3d.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "purchased_mugs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedMug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;            // Satın alan kullanıcı ile ilişki

    private Long completeMugId;   // Eğer tek bir tam model varsa onun ID'si
    private Long handleId;        // Kulp ID'si
    private Long mouthId;         // Ağızlık ID'si
    private Long bodyId;          // Gövde ID'si
    private Long designId;        // Desen ID'si

    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseDate;    // Satın alma tarihi

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;     // Kayıt oluşturulma zamanı
}
