package com.parking.parkinglotapplication.service.impl;

import com.parking.parkinglotapplication.dto.ParkingAssignmentDTO;
import com.parking.parkinglotapplication.dto.ParkingAvailabilityDTO;
import com.parking.parkinglotapplication.dto.ParkingReleaseDTO;
import com.parking.parkinglotapplication.dto.VehicleParkingDTO;
import com.parking.parkinglotapplication.entity.*;
import com.parking.parkinglotapplication.repository.ParkingHistoryRepository;
import com.parking.parkinglotapplication.repository.ParkingLotRepository;
import com.parking.parkinglotapplication.repository.ParkingSpaceRepository;
import com.parking.parkinglotapplication.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingHistoryRepository parkingHistoryRepository;

    // Base fee per hour
    private static final int BASE_FEE_TW = 10; // $10 per hour for two-wheelers
    private static final int BASE_FEE_FW = 20; // $20 per hour for four-wheelers

    @Override
    public List<ParkingAvailabilityDTO> getAvailableSpaces() {
        List<ParkingSpace> spaces = parkingSpaceRepository.findAll();
        List<ParkingAvailabilityDTO> availabilityList = new ArrayList<>();

        for (ParkingSpace space : spaces) {
            int twAvailable = parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    space.getLevel(), VehicleType.TW, Availability.AVAILABLE);

            int fwAvailable = parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    space.getLevel(), VehicleType.FW, Availability.AVAILABLE);

            ParkingAvailabilityDTO dto = new ParkingAvailabilityDTO();
            dto.setLevel(space.getLevel());
            dto.setTwoWheelerAvailable(twAvailable);
            dto.setFourWheelerAvailable(fwAvailable);

            availabilityList.add(dto);
        }

        return availabilityList;
    }

    @Override
    public Map<Integer, Boolean> getPublicAvailability(VehicleType vehicleType) {
        List<ParkingSpace> spaces = parkingSpaceRepository.findAll();
        Map<Integer, Boolean> levelAvailability = new HashMap<>();

        for (ParkingSpace space : spaces) {
            int available = parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    space.getLevel(), vehicleType, Availability.AVAILABLE);

            levelAvailability.put(space.getLevel(), available > 0);
        }

        return levelAvailability;
    }

    @Override
    @Transactional
    public ParkingAssignmentDTO assignParkingLot(VehicleParkingDTO parkingRequest) {
        // Validate if vehicle is already parked
        Optional<ParkingHistory> existingParking =
                parkingHistoryRepository.findByVehicleNumberAndOutTimeIsNull(parkingRequest.getVehicleNumber());

        if (existingParking.isPresent()) {
            throw new RuntimeException("Vehicle is already parked in the lot");
        }

        // Get parking space for the requested level
        ParkingSpace parkingSpace = parkingSpaceRepository.findByLevel(parkingRequest.getLevel());
        if (parkingSpace == null) {
            throw new RuntimeException("Invalid parking level");
        }

        // Find available parking lots for the requested vehicle type
        List<ParkingLot> availableLots = parkingLotRepository.findByParkingSpaceAndVehicleTypeAndAvailability(
                parkingSpace, parkingRequest.getVehicleType(), Availability.AVAILABLE);

        if (availableLots.isEmpty()) {
            throw new RuntimeException("No parking lots available for the requested vehicle type at level "
                    + parkingRequest.getLevel());
        }

        // Select a random lot
        Random random = new Random();
        ParkingLot selectedLot = availableLots.get(random.nextInt(availableLots.size()));

        // Update lot availability
        selectedLot.setAvailability(Availability.OCCUPIED);
        parkingLotRepository.save(selectedLot);

        // Update parking space availability count
        if (parkingRequest.getVehicleType() == VehicleType.TW) {
            parkingSpace.setTwa(parkingSpace.getTwa() - 1);
        } else {
            parkingSpace.setFwa(parkingSpace.getFwa() - 1);
        }
        parkingSpaceRepository.save(parkingSpace);

        // Create parking history entry
        ParkingHistory history = new ParkingHistory();
        history.setVehicleNumber(parkingRequest.getVehicleNumber());
        history.setVehicleType(parkingRequest.getVehicleType());
        history.setParkingLot(selectedLot);
        history.setLevel(parkingRequest.getLevel());
        history.setInTime(LocalDateTime.now());

        parkingHistoryRepository.save(history);

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Create response DTO
        ParkingAssignmentDTO response = new ParkingAssignmentDTO();
        response.setVehicleNumber(parkingRequest.getVehicleNumber());
        response.setVehicleType(parkingRequest.getVehicleType());
        response.setLevel(parkingRequest.getLevel());
        response.setLotNumber(selectedLot.getLotId());
        response.setLockingTime(history.getInTime());
        response.setUserId(userEmail);

        return response;
    }

    @Override
    @Transactional
    public ParkingReleaseDTO releaseParkingLot(String vehicleNumber, Long lotId) {
        // Find active parking for the vehicle
        ParkingHistory parkingHistory = parkingHistoryRepository.findByVehicleNumberAndOutTimeIsNull(vehicleNumber)
                .orElseThrow(() -> new RuntimeException("No active parking found for vehicle " + vehicleNumber));

        // Verify lot ID matches
        if (!parkingHistory.getParkingLot().getLotId().equals(lotId)) {
            throw new RuntimeException("Lot ID does not match the parked vehicle");
        }

        // Set exit time
        LocalDateTime exitTime = LocalDateTime.now();
        parkingHistory.setOutTime(exitTime);

        // Calculate fee
        int fee = calculateFee(parkingHistory.getVehicleType(), parkingHistory.getInTime(), exitTime);
        parkingHistory.setFee(fee);

        // Save updated history
        parkingHistoryRepository.save(parkingHistory);

        // Update lot availability
        ParkingLot parkingLot = parkingHistory.getParkingLot();
        parkingLot.setAvailability(Availability.AVAILABLE);
        parkingLotRepository.save(parkingLot);

        // Update parking space availability
        ParkingSpace parkingSpace = parkingLot.getParkingSpace();
        if (parkingHistory.getVehicleType() == VehicleType.TW) {
            parkingSpace.setTwa(parkingSpace.getTwa() + 1);
        } else {
            parkingSpace.setFwa(parkingSpace.getFwa() + 1);
        }
        parkingSpaceRepository.save(parkingSpace);

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Create response DTO
        ParkingReleaseDTO response = new ParkingReleaseDTO();
        response.setVehicleNumber(vehicleNumber);
        response.setLotNumber(lotId);
        response.setLockingTime(parkingHistory.getInTime());
        response.setUnlockingTime(exitTime);
        response.setFee(fee);
        response.setUserId(userEmail);

        return response;
    }

    private int calculateFee(VehicleType vehicleType, LocalDateTime inTime, LocalDateTime outTime) {
        // Calculate duration in hours (rounded up)
        long minutes = Duration.between(inTime, outTime).toMinutes();
        int hours = (int) Math.ceil(minutes / 60.0);

        // Calculate base fee
        int baseFee = (vehicleType == VehicleType.TW) ? BASE_FEE_TW : BASE_FEE_FW;

        return baseFee * hours;
    }
}