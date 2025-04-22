package com.project.po3d.business.abstracts;

import com.project.po3d.dto.category.CategoryRequest;
import com.project.po3d.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> createCategories(List<CategoryRequest> requests);
    List<CategoryResponse> getAllCategories();
}
