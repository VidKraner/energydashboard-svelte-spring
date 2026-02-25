package com.energydashboard.repository;

import com.energydashboard.model.ConsumptionReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsumptionReadingRepository extends JpaRepository<ConsumptionReading, Long> {
    List<ConsumptionReading> findByUserIdAndTimestampBetweenOrderByTimestampAsc(
            Long userId, LocalDateTime start, LocalDateTime end);
}