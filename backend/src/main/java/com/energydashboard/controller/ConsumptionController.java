package com.energydashboard.controller;

import com.energydashboard.model.ConsumptionReading;
import com.energydashboard.model.User;
import com.energydashboard.repository.ConsumptionReadingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consumption")
public class ConsumptionController {

    private final ConsumptionReadingRepository consumptionRepository;

    public ConsumptionController(ConsumptionReadingRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;
    }

    @GetMapping
    public ResponseEntity<List<ConsumptionReading>> getConsumption(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "24") int hours) {

        LocalDateTime start = LocalDateTime.now().minusHours(hours);
        LocalDateTime end = LocalDateTime.now();

        List<ConsumptionReading> readings = consumptionRepository
                .findByUserIdAndTimestampBetweenOrderByTimestampAsc(user.getId(), start, end);

        return ResponseEntity.ok(readings);
    }
}