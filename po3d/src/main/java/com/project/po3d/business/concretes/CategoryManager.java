package com.project.po3d.business.concretes;

import com.project.po3d.business.abstracts.CategoryService;
import com.project.po3d.dto.category.CategoryRequest;
import com.project.po3d.dto.category.CategoryResponse;
import com.project.po3d.entity.Category;
import com.project.po3d.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryManager implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public List<CategoryResponse> createCategories(List<CategoryRequest> requests) {
        List<String> categoryNames = requests.stream()
                .map(CategoryRequest::getName)
                .collect(Collectors.toList());

        List<Category> existingCategories = categoryRepository.findByNameIn(categoryNames);
        List<String> existingCategoryNames = existingCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        List<Category> newCategories = requests.stream()
                .filter(req -> !existingCategoryNames.contains(req.getName()))
                .map(req -> new Category(null, req.getName(), req.getDescription()))
                .collect(Collectors.toList());

        if (!newCategories.isEmpty()) {
            categoryRepository.saveAll(newCategories);
        }

        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryResponse(cat.getId(), cat.getName(), cat.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryResponse(cat.getId(), cat.getName(), cat.getDescription()))
                .collect(Collectors.toList());
    }
}
