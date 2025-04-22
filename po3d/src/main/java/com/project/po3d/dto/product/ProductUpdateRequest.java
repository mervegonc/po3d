package com.project.po3d.dto.product;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor // Tüm parametreleri alan bir constructor oluşturur
@NoArgsConstructor  // Parametresiz constructor oluşturur
public class ProductUpdateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Integer stock;

    @NotNull(message = "Kategori ID'si boş olamaz.") // Yeni kategori oluşturulamaz
    private UUID categoryId;
}
