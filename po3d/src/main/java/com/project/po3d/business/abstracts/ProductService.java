    package com.project.po3d.business.abstracts;

    import com.project.po3d.dto.product.ProductRequest;
    import com.project.po3d.dto.product.ProductResponse;
    import com.project.po3d.dto.product.ProductWithFilesResponse;
import com.project.po3d.dto.product.ProductWithFirstImageResponse;
import com.project.po3d.dto.product.UpdatedProductResponse;
import com.project.po3d.dto.product.ProductUpdateRequest;
import com.project.po3d.entity.ProductAttachment;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

    import java.util.List;
    import java.util.UUID;

    public interface ProductService {
        public ProductResponse createProduct(String name, String description, UUID categoryId, Double price, Integer stock, List<MultipartFile> files);

 List<ProductWithFirstImageResponse> getAllProducts();
 Resource getProductImage(UUID productId, String imageName);
//one product
 ProductWithFilesResponse getProductDetails(UUID id);
 List<String> getProductFileNames(UUID id);
 Resource getProductFile(UUID productId, String fileName);

 //edit

  UpdatedProductResponse updateProduct(UUID id, ProductUpdateRequest request);
 void addProductImage(UUID id, MultipartFile file);
 void deleteProductImage(UUID id, String fileName);
 void deleteProduct(UUID id);

       /* // ✅ Ürün güncelleme (Dosya ile)
        ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest, List<MultipartFile> files);

      
        ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest);
        void deleteProduct(UUID id);
        List<ProductResponse> getAllProducts();
        ProductResponse getProductById(UUID id);
        void deleteAllProducts();  

*/
    }