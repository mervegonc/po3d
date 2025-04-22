package com.project.po3d.controller;

import com.project.po3d.business.abstracts.ProductService;
import com.project.po3d.dto.product.ProductRequest;
import com.project.po3d.dto.product.ProductResponse;
import com.project.po3d.dto.product.ProductWithFilesResponse;
import com.project.po3d.dto.product.ProductWithFirstImageResponse;
import com.project.po3d.dto.product.UpdatedProductResponse;

import java.io.IOException;
import com.project.po3d.dto.product.ProductUpdateRequest;
import com.project.po3d.entity.Product;
import com.project.po3d.entity.ProductAttachment;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
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
   


    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") UUID categoryId,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        // **Kategori ID kontrolü**
        if (categoryId == null) {
            return ResponseEntity.badRequest().body(new ProductResponse(null, null, null, null, null, "Kategori ID boş olamaz!", null));
        }

        // **Servise gönder**
        ProductResponse response = productService.createProduct(name, description, categoryId, price, stock, files);

        return ResponseEntity.ok(response);
    }

 @GetMapping
    public ResponseEntity<List<ProductWithFirstImageResponse>> getAllProducts() {
        List<ProductWithFirstImageResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }



    @PreAuthorize("permitAll()")
    @GetMapping("/image/{productId}/{imageName}")
    public ResponseEntity<Resource> getProductImage(
            @PathVariable UUID productId,
            @PathVariable String imageName) {
        Resource image = productService.getProductImage(productId, imageName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg") // İçeriğin türü belirleniyor
                .body(image);
    }
    
//productdetail one page

      // 1️⃣ Ürünün detaylarını getir (adı, açıklaması, fiyatı, stok durumu, kategori, dosya URL'leri)
      @GetMapping("/{id}")
      public ResponseEntity<ProductWithFilesResponse> getProductDetails(@PathVariable UUID id) {
          return ResponseEntity.ok(productService.getProductDetails(id));
      }
  
      // 2️⃣ Ürünün dosya adlarını JSON formatında döndür
      @GetMapping("/files/{id}")
      public ResponseEntity<List<String>> getProductFileNames(@PathVariable UUID id) {
          return ResponseEntity.ok(productService.getProductFileNames(id));
      }
  
      // 3️⃣ Gerçek resim veya video dosyasını döndür
      @GetMapping("/file/{productId}/{fileName}")
      public ResponseEntity<Resource> getProductFile(
              @PathVariable UUID productId,
              @PathVariable String fileName) throws IOException {
          
          Resource resource = productService.getProductFile(productId, fileName);
          
          if (resource == null) {
              return ResponseEntity.notFound().build();
          }
  
          String contentType = Files.probeContentType(resource.getFile().toPath());
          if (contentType == null) {
              contentType = "application/octet-stream";
          }
  
          return ResponseEntity.ok()
                  .contentType(MediaType.parseMediaType(contentType))
                  .body(resource);
      }

//edit
 // 1️⃣ Ürün detaylarını güncelle (adı, fiyatı, açıklaması, kategorisi vs.)
@PutMapping("/{id}")
public ResponseEntity<UpdatedProductResponse> updateProduct(
        @PathVariable UUID id,
        @RequestBody @Valid ProductUpdateRequest request) {
    return ResponseEntity.ok(productService.updateProduct(id, request));
}


    // 2️⃣ Ürün resimleri ekleme & silme
    @PostMapping("/{id}/files")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        productService.addProductImage(id, file);
        return ResponseEntity.ok("Resim başarıyla yüklendi.");
    }

    @DeleteMapping("/{id}/files/{fileName}")
    public ResponseEntity<String> deleteProductImage(
            @PathVariable UUID id,
            @PathVariable String fileName) {
        productService.deleteProductImage(id, fileName);
        return ResponseEntity.ok("Resim başarıyla silindi.");
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Ürün ve ilgili tüm dosyalar başarıyla silindi.");
    }
    


    // Ürünün ilk görselini döndür (frontend bu API'yi kullanacak)
   

    /**
   
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


@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/upload")
public ResponseEntity<ProductResponse> uploadProduct(
        @RequestParam("productName") String productName,
        @RequestParam("description") String description,
        @RequestParam("category") String category,
        @RequestParam("price") Double price,
        @RequestParam("stock") Integer stock,
        @RequestParam("images") List<MultipartFile> images // 📌 Tek bir dosya yerine liste
) {
    ProductRequest productRequest = new ProductRequest(productName, description, category, price, stock);

    // 🔥 Güncellenmiş `createProduct` çağrısı (Liste olarak gönderiyoruz!)
    ProductResponse response = productService.createProduct(productRequest, images);

    return ResponseEntity.ok(response);
}


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest updateProductRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, updateProductRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-all")
    public ResponseEntity<String> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.ok("Tüm ürünler başarıyla silindi.");
    }*/
}
