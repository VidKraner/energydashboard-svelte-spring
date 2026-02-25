package com.energydashboard.config;

import com.energydashboard.model.ConsumptionReading;
import com.energydashboard.model.EnergyPrice;
import com.energydashboard.model.User;
import com.energydashboard.repository.ConsumptionReadingRepository;
import com.energydashboard.repository.EnergyPriceRepository;
import com.energydashboard.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConsumptionReadingRepository consumptionRepository;
    private final EnergyPriceRepository priceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      ConsumptionReadingRepository consumptionRepository,
                      EnergyPriceRepository priceRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.consumptionRepository = consumptionRepository;
        this.priceRepository = priceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // don't seed twice

        // Create demo user
        User demo = new User("demo@energypulse.com", passwordEncoder.encode("demo123"), "Demo User");
        userRepository.save(demo);

        // Seed 7 days of consumption readings (every 15 min)
        Random random = new Random(42);
        LocalDateTime now = LocalDateTime.now();

        for (int day = 6; day >= 0; day--) {
            for (int hour = 0; hour < 24; hour++) {
                for (int min = 0; min < 60; min += 15) {
                    ConsumptionReading reading = new ConsumptionReading();
                    reading.setUser(demo);
                    reading.setTimestamp(now.minusDays(day).withHour(hour).withMinute(min).withSecond(0));

                    // Simulate realistic usage: higher morning/evening, lower at night
                    double base = (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 21) ? 0.8 : 0.3;
                    if (hour >= 0 && hour <= 5) base = 0.1;
                    double kwh = base + (random.nextDouble() * 0.4);

                    reading.setKwh(Math.round(kwh * 100.0) / 100.0);
                    consumptionRepository.save(reading);
                }
            }
        }

        // Seed 24 hours of energy prices (hourly)
        for (int hour = 23; hour >= 0; hour--) {
            EnergyPrice price = new EnergyPrice();
            price.setTimestamp(now.minusHours(hour).withMinute(0).withSecond(0));

            // Simulate price fluctuation: higher during peak hours
            double basePrice = (hour >= 8 && hour <= 20) ? 0.25 : 0.12;
            double pricePerKwh = basePrice + (random.nextDouble() * 0.10);

            price.setPricePerKwh(Math.round(pricePerKwh * 1000.0) / 1000.0);
            priceRepository.save(price);
        }

        System.out.println("✓ Demo data seeded: user demo@energypulse.com / demo123");
    }
}