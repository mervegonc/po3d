package com.project.po3d.business.concretes;

import com.project.po3d.business.abstracts.ProductService;
import com.project.po3d.repository.ProductRepository;
import com.project.po3d.dto.product.ProductRequest;
import com.project.po3d.dto.product.ProductResponse;
import com.project.po3d.dto.product.UpdateProductRequest;
import com.project.po3d.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductManager implements ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest, MultipartFile image) {
        try {
            // Doğru upload yolu
            String uploadDir = "C:/pothreedy/po3d/po3d/uploads/products";  
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
    
            // Benzersiz dosya adı oluştur
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            
            // Doğru path oluştur
            File file = new File(uploadDir, fileName);  // Burada düzelttik
            
            // Dosyayı kaydet
            image.transferTo(file);
    
            // Ürünü oluştur ve kaydet
            Product product = new Product();
            product.setName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setCategory(productRequest.getCategory());
            product.setPrice(productRequest.getPrice());
            product.setStock(productRequest.getStock());
            product.setImages(List.of(uploadDir + "/" + fileName)); // Doğru path formatı
    
            productRepository.save(product);
    
            return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                    product.getCategory(), product.getPrice(), product.getStock(), product.getImages());
        } catch (IOException e) {
            throw new RuntimeException("Ürün kaydedilirken hata oluştu: " + e.getMessage());
        }
    }
    

    
@Override
public ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest, MultipartFile image) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ürün bulunamadı."));

    // Güncellenen alanları ayarla
    if (updateProductRequest.getProductName() != null) {
        product.setName(updateProductRequest.getProductName());
    }
    if (updateProductRequest.getDescription() != null) {
        product.setDescription(updateProductRequest.getDescription());
    }
    if (updateProductRequest.getCategory() != null) {
        product.setCategory(updateProductRequest.getCategory());
    }
    if (updateProductRequest.getPrice() != null) {
        product.setPrice(updateProductRequest.getPrice());
    }
    if (updateProductRequest.getStock() != null) {
        product.setStock(updateProductRequest.getStock());
    }

    // Eğer yeni bir resim yüklenmişse, eskisini sil ve yenisini kaydet
    if (image != null && !image.isEmpty()) {
        try {
            // Önceki dosyayı sil
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                File oldFile = new File(product.getImages().get(0));
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // Yeni dosyayı kaydet
            String uploadDir = "C:/pothreedy/po3d/po3d/uploads/products";
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            File file = new File(uploadDir, fileName);
            image.transferTo(file);

            // Yeni resim yolunu kaydet
            product.setImages(List.of(uploadDir + "/" + fileName));

        } catch (IOException e) {
            throw new RuntimeException("Resim güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    // Güncellenmiş ürünü kaydet
    productRepository.save(product);

    return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
            product.getCategory(), product.getPrice(), product.getStock(), product.getImages());
}





@Override
public ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest) {
    // 1. Güncellenecek ürünü bul
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ürün bulunamadı."));

    // 2. Güncellenebilir alanları set et
    if (updateProductRequest.getProductName() != null) product.setName(updateProductRequest.getProductName());
    if (updateProductRequest.getDescription() != null) product.setDescription(updateProductRequest.getDescription());
    if (updateProductRequest.getCategory() != null) product.setCategory(updateProductRequest.getCategory());
    if (updateProductRequest.getPrice() != null) product.setPrice(updateProductRequest.getPrice());
    if (updateProductRequest.getStock() != null) product.setStock(updateProductRequest.getStock());

    // 3. Güncellenmiş ürünü kaydet
    productRepository.save(product);

    // 4. Response dön
    return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getCategory(),
            product.getPrice(),
            product.getStock(),
            product.getImages()
    );
}
@Override
public void deleteProduct(UUID id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ürün bulunamadı."));
    productRepository.delete(product);
}











}
