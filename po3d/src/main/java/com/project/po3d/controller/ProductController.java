package com.project.po3d.controller;

import com.project.po3d.business.abstracts.ProductService;
import com.project.po3d.dto.product.ProductRequest;
import com.project.po3d.dto.product.ProductResponse;
import com.project.po3d.dto.product.UpdateProductRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/upload")
    public ResponseEntity<ProductResponse> uploadProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("image") MultipartFile image) {
        
        ProductRequest productRequest = new ProductRequest(productName, description, category, price, stock);
        ProductResponse response = productService.createProduct(productRequest, image);
        return ResponseEntity.ok(response);
    }




    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest updateProductRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, updateProductRequest);
        return ResponseEntity.ok(updatedProduct);
    }
    





    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Ürün başarıyla silindi.");
    }
    













}
