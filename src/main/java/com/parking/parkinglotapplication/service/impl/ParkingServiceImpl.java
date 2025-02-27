package com.parking.parkinglotapplication.service.impl;

import com.parking.parkinglotapplication.dto.AvailabilityResponse;
import com.parking.parkinglotapplication.dto.ParkingAssignmentRequest;
import com.parking.parkinglotapplication.dto.ParkingAssignmentResponse;
import com.parking.parkinglotapplication.dto.ParkingUnlockRequest;
import com.parking.parkinglotapplication.dto.ParkingUnlockResponse;
import com.parking.parkinglotapplication.entity.Availability;
import com.parking.parkinglotapplication.entity.ParkingHistory;
import com.parking.parkinglotapplication.entity.ParkingLot;
import com.parking.parkinglotapplication.entity.ParkingSpace;
import com.parking.parkinglotapplication.entity.VehicleType;
import com.parking.parkinglotapplication.exception.ParkingException;
import com.parking.parkinglotapplication.repository.ParkingHistoryRepository;
import com.parking.parkinglotapplication.repository.ParkingLotRepository;
import com.parking.parkinglotapplication.repository.ParkingSpaceRepository;
import com.parking.parkinglotapplication.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingHistoryRepository parkingHistoryRepository;

    private static final int BASE_FEE_PER_HOUR = 20; // Base fee in Rs per hour
    private static final int LATE_FEE_MULTIPLIER = 2; // Additional fee multiplier for late checkout

    @Override
    public Map<Integer, AvailabilityResponse> getAvailabilityByLevel() {
        List<ParkingSpace> allParkingSpaces = parkingSpaceRepository.findAll();
        Map<Integer, AvailabilityResponse> availabilityMap = new HashMap<>();

        for (ParkingSpace space : allParkingSpaces) {
            int level = space.getLevel();
            int twAvailable = parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    level, VehicleType.TW, Availability.AVAILABLE);
            int fwAvailable = parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    level, VehicleType.FW, Availability.AVAILABLE);

            availabilityMap.put(level, new AvailabilityResponse(twAvailable, fwAvailable));
        }

        return availabilityMap;
    }

    @Override
    public Map<Integer, Boolean> getPublicAvailabilityByLevel(String vehicleType) {
        List<ParkingSpace> allParkingSpaces = parkingSpaceRepository.findAll();
        Map<Integer, Boolean> availabilityMap = new HashMap<>();
        VehicleType type = VehicleType.valueOf(vehicleType);

        for (ParkingSpace space : allParkingSpaces) {
            int level = space.getLevel();
            int available = parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    level, type, Availability.AVAILABLE);
            availabilityMap.put(level, available > 0);
        }

        return availabilityMap;
    }

    @Override
    @Transactional
    public ParkingAssignmentResponse assignParkingSpace(ParkingAssignmentRequest request) {
        // Validate input
        if (request.getVehicleNumber() == null || request.getVehicleNumber().trim().isEmpty()) {
            throw new ParkingException("Vehicle number cannot be empty");
        }

        // Check if vehicle is already parked
        Optional<ParkingHistory> existingParking = parkingHistoryRepository.findByVehicleNumberAndOutTimeIsNull(request.getVehicleNumber());
        if (existingParking.isPresent()) {
            throw new ParkingException("Vehicle is already parked");
        }

        // Get parking space for the requested level
        ParkingSpace parkingSpace = parkingSpaceRepository.findByLevel(request.getLevel());
        if (parkingSpace == null) {
            throw new ParkingException("Parking level not found");
        }

        // Get all available parking lots for the requested vehicle type in the specified level
        VehicleType vehicleType = VehicleType.valueOf(request.getVehicleType());
        List<ParkingLot> availableLots = parkingLotRepository.findByParkingSpaceAndVehicleTypeAndAvailability(
                parkingSpace, vehicleType, Availability.AVAILABLE);

        if (availableLots.isEmpty()) {
            throw new ParkingException("No available parking lots for the requested vehicle type");
        }

        // Randomly select a parking lot
        Random random = new Random();
        ParkingLot selectedLot = availableLots.get(random.nextInt(availableLots.size()));

        // Update parking lot status to OCCUPIED
        selectedLot.setAvailability(Availability.OCCUPIED);
        parkingLotRepository.save(selectedLot);

        // Update available slot count in parking space
        if (vehicleType == VehicleType.TW) {
            parkingSpace.setTwa(parkingSpace.getTwa() - 1);
        } else {
            parkingSpace.setFwa(parkingSpace.getFwa() - 1);
        }
        parkingSpaceRepository.save(parkingSpace);

        // Create parking history entry
        ParkingHistory parkingHistory = new ParkingHistory();
        parkingHistory.setVehicleNumber(request.getVehicleNumber());
        parkingHistory.setVehicleType(vehicleType);
        parkingHistory.setParkingLot(selectedLot);
        parkingHistory.setLevel(request.getLevel());
        parkingHistory.setInTime(LocalDateTime.now());
        parkingHistoryRepository.save(parkingHistory);

        // Create response
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ParkingAssignmentResponse(
                request.getVehicleType(),
                request.getVehicleNumber(),
                request.getLevel(),
                selectedLot.getLotId(),
                parkingHistory.getInTime(),
                currentUser
        );
    }

    @Override
    @Transactional
    public ParkingUnlockResponse unlockParkingSpace(ParkingUnlockRequest request) {
        // Validate input
        if (request.getVehicleNumber() == null || request.getVehicleNumber().trim().isEmpty()) {
            throw new ParkingException("Vehicle number cannot be empty");
        }

        // Find parking history for the vehicle
        ParkingHistory parkingHistory = parkingHistoryRepository.findByVehicleNumberAndOutTimeIsNull(request.getVehicleNumber())
                .orElseThrow(() -> new ParkingException("No active parking found for the vehicle"));

        // Verify lot matches if provided
        if (request.getLotId() != null && !parkingHistory.getParkingLot().getLotId().equals(request.getLotId())) {
            throw new ParkingException("Vehicle is not parked in the specified lot");
        }

        // Update parking lot status to AVAILABLE
        ParkingLot parkingLot = parkingHistory.getParkingLot();
        parkingLot.setAvailability(Availability.AVAILABLE);
        parkingLotRepository.save(parkingLot);

        // Update available slot count in parking space
        ParkingSpace parkingSpace = parkingLot.getParkingSpace();
        if (parkingHistory.getVehicleType() == VehicleType.TW) {
            parkingSpace.setTwa(parkingSpace.getTwa() + 1);
        } else {
            parkingSpace.setFwa(parkingSpace.getFwa() + 1);
        }
        parkingSpaceRepository.save(parkingSpace);

        // Calculate fee
        LocalDateTime outTime = LocalDateTime.now();
        parkingHistory.setOutTime(outTime);

        long durationHours = Duration.between(parkingHistory.getInTime(), outTime).toHours();
        if (durationHours < 1) durationHours = 1; // Minimum 1 hour charge

        int fee = calculateFee(parkingHistory.getVehicleType(), durationHours);
        parkingHistory.setFee(fee);
        parkingHistoryRepository.save(parkingHistory);

        // Create response
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ParkingUnlockResponse(
                parkingHistory.getVehicleNumber(),
                parkingLot.getLotId(),
                parkingHistory.getInTime(),
                outTime,
                fee,
                currentUser
        );
    }

    private int calculateFee(VehicleType vehicleType, long hours) {
        int baseHourlyRate = vehicleType == VehicleType.TW ? BASE_FEE_PER_HOUR : BASE_FEE_PER_HOUR * 2;
        return (int) (baseHourlyRate * hours);
    }
}