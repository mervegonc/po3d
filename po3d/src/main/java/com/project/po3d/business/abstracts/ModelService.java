package com.project.po3d.business.abstracts;
import org.springframework.core.io.Resource;

import java.util.List;

import com.project.po3d.entity.PurchasedMug;
import com.project.po3d.entity.SavedMug;

public interface ModelService {
  List<PurchasedMug> getAllPurchasedMugs();
    PurchasedMug getPurchasedMugById(Long id);
     PurchasedMug purchaseMug(PurchasedMug mug);  // Ödeme sonrası satın alınan mug kaydı
    SavedMug saveMug(SavedMug mug);
    Resource getModelPart(String partType, String partName);
}
