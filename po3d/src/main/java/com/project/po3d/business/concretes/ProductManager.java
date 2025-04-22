package com.project.po3d.business.concretes;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import java.util.NoSuchElementException;
import com.project.po3d.business.abstracts.ProductService;
import com.project.po3d.dto.product.ProductRequest;
import com.project.po3d.dto.product.ProductResponse;
import com.project.po3d.dto.product.ProductWithFilesResponse;
import com.project.po3d.dto.product.ProductWithFirstImageResponse;
import com.project.po3d.dto.product.UpdatedProductResponse;

import com.project.po3d.dto.product.ProductUpdateRequest;
import com.project.po3d.entity.Category;
import com.project.po3d.entity.Product;
import com.project.po3d.entity.ProductAttachment;
import com.project.po3d.exception.ResourceNotFoundException;
import com.project.po3d.repository.CategoryRepository;
import com.project.po3d.repository.ProductAttachmentRepository;
import com.project.po3d.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import java.util.Collections;

import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.Resource;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductManager implements ProductService {
    private final ProductRepository productRepository;
    private final ProductAttachmentRepository productAttachmentRepository;
    private final CategoryRepository categoryRepository;
    private final String uploadDir = "uploads";  // **Dosya yükleme dizini**

 
    @Transactional
    @Override
    public ProductResponse createProduct(String name, String description, UUID categoryId, Double price, Integer stock, List<MultipartFile> files) {
        // ✅ Kategori Kontrolü
        Category category = categoryRepository.findById(categoryId)
                .orElse(null);
    
        if (category == null) {
            return new ProductResponse(null, null, null, null, null, "Kategori bulunamadı!", null);
        }
    
        // ✅ Yeni Ürün Oluştur
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        
        // 🔥 Ürünü önce kaydet!
        product = productRepository.save(product);
    
        // ✅ Ürün İçin Klasör Oluştur
        String productFolderPath = uploadDir + "/" + product.getId();
        File productFolder = new File(productFolderPath);
        if (!productFolder.exists()) {
            productFolder.mkdirs();
        }
    
        // ✅ Dosyaları Kaydet (UUID kullanarak)
        List<String> savedFileUrls = saveAttachments(product, files, productFolderPath);
    
        // ✅ Yanıt Dön
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                category.getName(),
                savedFileUrls
        );
    }
    
    private List<String> saveAttachments(Product product, List<MultipartFile> files, String productFolderPath) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }
    
        return files.stream().map(file -> {
            try {
                // ✅ **Benzersiz dosya adı oluştur (UUID)**
                String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                Path filePath = Paths.get(productFolderPath, uniqueFileName).toAbsolutePath();
                
                // ✅ **Dosyayı kaydet**
                file.transferTo(filePath.toFile());
    
                // ✅ **Dosyanın türünü belirle (image/video)**
                String fileType = file.getContentType().startsWith("image") ? "image" : "video";
    
                // ✅ **Veritabanına kaydet**
                ProductAttachment attachment = new ProductAttachment();
                attachment.setProduct(product);
                attachment.setFileUrl(uniqueFileName); // 🔥 **UUID ile oluşturulan isim**
                attachment.setFileType(fileType);
                productAttachmentRepository.save(attachment);
    
                return uniqueFileName;
            } catch (IOException e) {
                throw new RuntimeException("Dosya kaydedilirken hata oluştu: " + e.getMessage());
            }
        }).collect(Collectors.toList());
    }
    
      
    
    @Override
    public List<ProductWithFirstImageResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        
        return products.stream().map(product -> {
            // Ürün bilgilerini al
            String firstImageUrl = productAttachmentRepository
                                        .findByProductId(product.getId())
                                        .stream()
                                        .findFirst() // İlk resim
                                        .map(ProductAttachment::getFileUrl) // Resmin URL'sini al
                                        .orElse(null); // Eğer resim yoksa null döner

            // ProductWithFirstImageResponse DTO'yu oluştur
            return new ProductWithFirstImageResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getName(),
                firstImageUrl
            );
        }).collect(Collectors.toList());
    }

   @Override
public Resource getProductImage(UUID productId, String imageName) {
    try {
        // Dosyanın bulunduğu yolu belirle
        Path imagePath = Paths.get(uploadDir)
                .resolve(productId.toString()) // Ürüne ait klasör
                .resolve(imageName) // Resmin kendisi
                .normalize();

        // Dosya varsa, kaynağı döndür
        Resource resource = new UrlResource(imagePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resim bulunamadı.");
        }
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Resim yüklenirken hata oluştu.");
    }
}

// productdetailpage one
  // 1️⃣ Ürün bilgileri ve dosya URL'lerini getir
  @Override
  public ProductWithFilesResponse getProductDetails(UUID id) {
      Product product = productRepository.findById(id)
              .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı"));

      List<String> fileUrls = productAttachmentRepository.findByProductId(id)
              .stream()
              .map(ProductAttachment::getFileUrl)
              .collect(Collectors.toList());

      return new ProductWithFilesResponse(
              product.getId(),
              product.getName(),
              product.getDescription(),
              product.getPrice(),
              product.getStock(),
              product.getCategory().getName(),
              fileUrls
      );
  }

  // 2️⃣ Ürünün dosya adlarını JSON formatında döndür
  @Override
  public List<String> getProductFileNames(UUID id) {
      File productFolder = new File(uploadDir + "/" + id);
      if (!productFolder.exists() || !productFolder.isDirectory()) {
          throw new ResourceNotFoundException("Ürüne ait dosyalar bulunamadı.");
      }

      return Arrays.stream(productFolder.listFiles())
              .filter(File::isFile)
              .map(File::getName)
              .collect(Collectors.toList());
  }

  // 3️⃣ Gerçek resim veya video dosyasını döndür
  @Override
  public Resource getProductFile(UUID productId, String fileName) {
      try {
          Path filePath = Paths.get(uploadDir)
                  .resolve(productId.toString())
                  .resolve(fileName)
                  .normalize();

          Resource resource = new UrlResource(filePath.toUri());

          if (!resource.exists() || !resource.isReadable()) {
              return null;
          }
          return resource;
      } catch (Exception e) {
          throw new RuntimeException("Dosya yüklenemedi: " + fileName, e);
      }
  }

//edit

  // 1️⃣ Ürün Güncelleme
 @Override
public UpdatedProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı"));

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());

    // 🔹 Kategori doğrulaması: Sadece var olan kategoriler seçilebilir
    Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Geçersiz kategori ID!"));

    product.setCategory(category);

    productRepository.save(product);
    return new UpdatedProductResponse(product);
}

    // 2️⃣ Resim Ekleme
    @Override
    public void addProductImage(UUID id, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı"));

        try {
            Path productPath = Paths.get(uploadDir, id.toString());
            if (!Files.exists(productPath)) {
                Files.createDirectories(productPath);
            }

            Path filePath = productPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Dosya yüklenirken hata oluştu.", e);
        }
    }

    // 3️⃣ Resim Silme
    @Override
    public void deleteProductImage(UUID id, String fileName) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı"));

        Path filePath = Paths.get(uploadDir, id.toString(), fileName);
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new ResourceNotFoundException("Resim bulunamadı.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Resim silinirken hata oluştu.", e);
        }
    }
@Transactional
@Override
public void deleteProduct(UUID id) {
    // ✅ **Ürün kontrolü**
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı!"));

    // ✅ **İlgili dosyaları sil**
    String productFolderPath = uploadDir + "/" + id;
    File productFolder = new File(productFolderPath);

    if (productFolder.exists() && productFolder.isDirectory()) {
        try {
            FileUtils.deleteDirectory(productFolder); // 🔥 **Apache Commons IO ile tüm klasörü sil**
        } catch (IOException e) {
            throw new RuntimeException("Dosya klasörü silinemedi: " + e.getMessage());
        }
    }

    // ✅ **Bağlı attachment kayıtlarını sil**
    productAttachmentRepository.deleteByProductId(id);

    // ✅ **Ürünü veritabanından sil**
    productRepository.delete(product);
}


    }
/* 
    // ✅ Ürün güncelleme (Dosya ile)
    @Override
    public ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest, List<MultipartFile> files) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı."));

        if (updateProductRequest.getProductName() != null) product.setName(updateProductRequest.getProductName());
        if (updateProductRequest.getDescription() != null) product.setDescription(updateProductRequest.getDescription());
        if (updateProductRequest.getCategory() != null) product.setCategory(updateProductRequest.getCategory());
        if (updateProductRequest.getPrice() != null) product.setPrice(updateProductRequest.getPrice());
        if (updateProductRequest.getStock() != null) product.setStock(updateProductRequest.getStock());

        productRepository.save(product);

        if (files != null && !files.isEmpty()) {
            String productFolderPath = uploadDir + "/" + product.getId();

            // Eski dosyaları sil
            deleteAttachments(product.getId());

            // Yeni dosyaları kaydet
            List<String> savedFileUrls = saveAttachments(product, files, productFolderPath);

            return new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getStock(),
                    savedFileUrls);
        }

        return getProductById(product.getId());
    }

    // ✅ Ürün güncelleme (Dosya olmadan)
    @Override
    public ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı."));

        if (updateProductRequest.getProductName() != null) product.setName(updateProductRequest.getProductName());
        if (updateProductRequest.getDescription() != null) product.setDescription(updateProductRequest.getDescription());
        if (updateProductRequest.getCategory() != null) product.setCategory(updateProductRequest.getCategory());
        if (updateProductRequest.getPrice() != null) product.setPrice(updateProductRequest.getPrice());
        if (updateProductRequest.getStock() != null) product.setStock(updateProductRequest.getStock());

        productRepository.save(product);

        return getProductById(product.getId());
    }

    // ✅ Ürün silme
    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı."));

        deleteAttachments(id);
        productRepository.delete(product);
    }

    // ✅ Tüm ürünleri getirme
    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    List<String> fileUrls = productAttachmentRepository.findByProductId(product.getId())
                            .stream()
                            .map(ProductAttachment::getFileUrl)
                            .collect(Collectors.toList());

                    return new ProductResponse(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getCategory(),
                            product.getPrice(),
                            product.getStock(),
                            fileUrls);
                })
                .collect(Collectors.toList());
    }

    // ✅ Belirli bir ürünü getirme
    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı."));

        List<String> fileUrls = productAttachmentRepository.findByProductId(product.getId())
                .stream()
                .map(ProductAttachment::getFileUrl)
                .collect(Collectors.toList());

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                fileUrls);
    }

    // ✅ Tüm ürünleri silme
    @Override
    @Transactional
    public void deleteAllProducts() {
        productAttachmentRepository.deleteAll();
        productRepository.deleteAll();

        File directory = new File(uploadDir);
        if (directory.exists()) {
            deleteDirectory(directory.toPath());
        }
    }

   


    // 📌 Yardımcı Metod: Ürün dosyalarını silme
    private void deleteAttachments(UUID productId) {
        List<ProductAttachment> attachments = productAttachmentRepository.findByProductId(productId);

        for (ProductAttachment attachment : attachments) {
            File file = new File(attachment.getFileUrl());
            if (file.exists()) {
                file.delete();
            }
        }

        productAttachmentRepository.deleteByProductId(productId);
    }

    // 📌 Yardımcı Metod: Klasör silme
    private void deleteDirectory(Path path) {
        try {
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(file -> file.toFile().delete());
            }
        } catch (Exception e) {
            System.err.println("Klasör silme hatası: " + e.getMessage());
        }
    }*/

