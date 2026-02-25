package com.energydashboard.repository;

import com.energydashboard.model.EnergyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EnergyPriceRepository extends JpaRepository<EnergyPrice, Long> {
    Optional<EnergyPrice> findTopByOrderByTimestampDesc();
}