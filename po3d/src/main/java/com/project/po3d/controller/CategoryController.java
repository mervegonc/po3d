package com.project.po3d.controller;

import com.project.po3d.business.abstracts.CategoryService;
import com.project.po3d.dto.category.CategoryRequest;
import com.project.po3d.dto.category.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCategories(@RequestBody List<CategoryRequest> requests) {
        List<CategoryResponse> savedCategories = categoryService.createCategories(requests);
        return ResponseEntity.ok(Map.of("message", "Categories successfully created", "categories", savedCategories));
    }


    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

}
