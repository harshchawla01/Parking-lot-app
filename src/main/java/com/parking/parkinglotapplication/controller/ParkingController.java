package com.parking.parkinglotapplication.controller;

import com.parking.parkinglotapplication.dto.AvailabilityResponse;
import com.parking.parkinglotapplication.dto.ParkingAssignmentRequest;
import com.parking.parkinglotapplication.dto.ParkingAssignmentResponse;
import com.parking.parkinglotapplication.dto.ParkingUnlockRequest;
import com.parking.parkinglotapplication.dto.ParkingUnlockResponse;
import com.parking.parkinglotapplication.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    /**
     * Admin endpoint to get detailed availability information
     * @return Map of level to availability details
     */
    @GetMapping("/availability/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<Integer, AvailabilityResponse>> getAdminAvailability() {
        return ResponseEntity.ok(parkingService.getAvailabilityByLevel());
    }

    /**
     * Public endpoint to get simplified availability information
     * @param vehicleType The type of vehicle (TW or FW)
     * @return Map of level to boolean availability
     */
    @GetMapping("/availability/public/{vehicleType}")
    public ResponseEntity<Map<Integer, Boolean>> getPublicAvailability(@PathVariable String vehicleType) {
        return ResponseEntity.ok(parkingService.getPublicAvailabilityByLevel(vehicleType));
    }

    /**
     * Assign a parking space
     * @param request The assignment request with vehicle details
     * @return The assignment response with lot details
     */
    @PostMapping("/assign")
    public ResponseEntity<ParkingAssignmentResponse> assignParkingSpace(@RequestBody ParkingAssignmentRequest request) {
        return ResponseEntity.ok(parkingService.assignParkingSpace(request));
    }

    /**
     * Unlock a parking space
     * @param request The unlock request with vehicle details
     * @return The unlock response with fee details
     */
    @PostMapping("/unlock")
    public ResponseEntity<ParkingUnlockResponse> unlockParkingSpace(@RequestBody ParkingUnlockRequest request) {
        return ResponseEntity.ok(parkingService.unlockParkingSpace(request));
    }
}