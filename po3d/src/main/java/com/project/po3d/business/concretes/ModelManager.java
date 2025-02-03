package com.project.po3d.business.concretes;

import com.project.po3d.business.abstracts.ModelService;
import com.project.po3d.entity.PurchasedMug;
import com.project.po3d.entity.SavedMug;
import com.project.po3d.repository.ModelRepository;
import com.project.po3d.repository.SavedMugRepository;

import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;




@Service
public class ModelManager implements ModelService {

    private final ModelRepository modelRepository;
    private final SavedMugRepository savedMugRepository;


    public ModelManager(ModelRepository modelRepository, SavedMugRepository savedMugRepository) {
        this.modelRepository = modelRepository;
        this.savedMugRepository = savedMugRepository;
    }


    @Value("${app.models.path}")
    private String modelsPath;

    @Override
    public Resource getModelPart(String partType, String partName) {
        try {
            Path filePath = Paths.get(modelsPath + "/" + partType + "/" + partName + ".glb");
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Dosya bulunamadı veya okunamıyor: " + partName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Dosya yolu hatalı: " + partName, e);
        }
    }





    @Override
    public List<PurchasedMug> getAllPurchasedMugs() {
        return modelRepository.findAll();
    }

    @Override
    public PurchasedMug getPurchasedMugById(Long id) {
        return modelRepository.findById(id).orElse(null);
    }

    @Override
    public PurchasedMug purchaseMug(PurchasedMug mug) {
        // Satın alınan mug'ı veritabanına kaydet
        return modelRepository.save(mug);
    }

    @Override
    public SavedMug saveMug(SavedMug mug) {
        // Kaydedilen mug'ı veritabanına kaydet
        return savedMugRepository.save(mug);
    }
}
