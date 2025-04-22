package com.project.po3d.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoryRequest {
    private String name;
    private String description;
}
