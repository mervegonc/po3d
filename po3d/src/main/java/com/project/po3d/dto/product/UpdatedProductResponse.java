package com.project.po3d.dto.product;
import java.util.UUID;

import com.project.po3d.entity.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@AllArgsConstructor // Tüm parametreleri alan bir constructor oluşturur
@NoArgsConstructor  // Parametresiz constructor oluşturur
public class UpdatedProductResponse  {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String categoryName;


        public UpdatedProductResponse(Product product) {
        this.id = product.getId().toString();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.categoryName = product.getCategory().getName();
    }
}
