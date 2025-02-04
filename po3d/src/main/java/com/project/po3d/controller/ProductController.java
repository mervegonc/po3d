package com.project.po3d.controller;

import com.project.po3d.business.abstracts.ProductService;
import com.project.po3d.dto.product.ProductRequest;
import com.project.po3d.dto.product.ProductResponse;
import com.project.po3d.dto.product.UpdateProductRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final String uploadDir = "C:/pothreedy/po3d/po3d/uploads/products/";

    /**
     * Ürün görselini almak için endpoint.
     */
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Tüm kullanıcılar tarafından erişilebilir: Ürünleri listeleme.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Sadece ADMIN yetkisine sahip kullanıcılar ürün ekleyebilir.
     */
    @PreAuthorize("hasRole('ADMIN')")
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

    /**
     * Sadece ADMIN yetkisine sahip kullanıcılar ürün güncelleyebilir.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest updateProductRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, updateProductRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Sadece ADMIN yetkisine sahip kullanıcılar ürün silebilir.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Ürün başarıyla silindi.");
    }
}
