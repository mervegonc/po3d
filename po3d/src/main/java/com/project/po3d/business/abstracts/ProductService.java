package com.project.po3d.business.abstracts;

import com.project.po3d.dto.product.ProductRequest;
import com.project.po3d.dto.product.ProductResponse;
import com.project.po3d.dto.product.UpdateProductRequest;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest, MultipartFile image);

     ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest, MultipartFile image);
     ProductResponse updateProduct(UUID id, UpdateProductRequest updateProductRequest);
     void deleteProduct(UUID id);
     List<ProductResponse> getAllProducts();
     ProductResponse getProductById(UUID id);

}
