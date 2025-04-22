package com.project.po3d.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
// ProductWithFirstImageResponse.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithFirstImageResponse {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String categoryName;
    private String firstImageUrl; // İlk görselin URL'si
}

