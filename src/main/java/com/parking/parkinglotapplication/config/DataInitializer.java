package com.parking.parkinglotapplication.config;

import com.parking.parkinglotapplication.entity.*;
import com.parking.parkinglotapplication.repository.ParkingLotRepository;
import com.parking.parkinglotapplication.repository.ParkingSpaceRepository;
import com.parking.parkinglotapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (parkingSpaceRepository.count() > 0) {
            return;
        }

        // Create default admin user
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@parking.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Create default regular user
        User user = new User();
        user.setName("User");
        user.setEmail("user@parking.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole(Role.USER);
        userRepository.save(user);

        // Create parking levels (3 levels)
        for (int level = 1; level <= 3; level++) {
            ParkingSpace parkingSpace = new ParkingSpace();
            parkingSpace.setId(level);
            parkingSpace.setLevel(level);
            parkingSpace.setTwa(10); // 10 two-wheeler slots per level
            parkingSpace.setFwa(20); // 20 four-wheeler slots per level

            // Create list to hold parking lots
            List<ParkingLot> parkingLots = new ArrayList<>();

            // Create two-wheeler parking lots
            for (int i = 1; i <= 10; i++) {
                ParkingLot parkingLot = new ParkingLot();
                parkingLot.setVehicleType(VehicleType.TW);
                parkingLot.setAvailability(Availability.AVAILABLE);
                parkingLot.setParkingSpace(parkingSpace);
                parkingLots.add(parkingLot);
            }

            // Create four-wheeler parking lots
            for (int i = 1; i <= 20; i++) {
                ParkingLot parkingLot = new ParkingLot();
                parkingLot.setVehicleType(VehicleType.FW);
                parkingLot.setAvailability(Availability.AVAILABLE);
                parkingLot.setParkingSpace(parkingSpace);
                parkingLots.add(parkingLot);
            }

            parkingSpace.setParkingLots(parkingLots);
            parkingSpaceRepository.save(parkingSpace);

            // Save all parking lots
            parkingLotRepository.saveAll(parkingLots);
        }
    }
}