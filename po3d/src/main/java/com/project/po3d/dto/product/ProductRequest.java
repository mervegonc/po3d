package com.project.po3d.dto.product;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Tüm parametreleri alan bir constructor oluşturur
@NoArgsConstructor  // Parametresiz constructor oluşturur
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private UUID categoryId;
}
