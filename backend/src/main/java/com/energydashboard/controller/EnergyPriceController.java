package com.energydashboard.controller;

import com.energydashboard.model.EnergyPrice;
import com.energydashboard.repository.EnergyPriceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prices")
public class EnergyPriceController {

    private final EnergyPriceRepository priceRepository;

    public EnergyPriceController(EnergyPriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPrice() {
        return priceRepository.findTopByOrderByTimestampDesc()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}