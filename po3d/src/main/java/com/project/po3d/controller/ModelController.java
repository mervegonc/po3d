package com.project.po3d.controller;

import com.project.po3d.business.abstracts.ModelService;
import com.project.po3d.entity.PurchasedMug;
import com.project.po3d.entity.SavedMug;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }


    @GetMapping("/{partType}/{partName}")
    public ResponseEntity<Resource> getModelPart(@PathVariable String partType, @PathVariable String partName) {
        Resource resource = modelService.getModelPart(partType, partName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }



    @PostMapping("/purchase")
    public ResponseEntity<PurchasedMug> purchaseMug(@RequestBody PurchasedMug mug) {
        PurchasedMug savedMug = modelService.purchaseMug(mug);
        return ResponseEntity.ok(savedMug);
    }

    @PostMapping("/save")
    public ResponseEntity<SavedMug> saveMug(@RequestBody SavedMug mug) {
        SavedMug savedMug = modelService.saveMug(mug);
        return ResponseEntity.ok(savedMug);
    }
}

    
