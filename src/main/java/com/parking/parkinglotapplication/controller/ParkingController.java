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

    @GetMapping("/availability/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<Integer, AvailabilityResponse>> getAdminAvailability() {
        return ResponseEntity.ok(parkingService.getAvailabilityByLevel());
    }

    @GetMapping("/availability/public/{vehicleType}")
    public ResponseEntity<Map<Integer, Boolean>> getPublicAvailability(@PathVariable String vehicleType) {
        return ResponseEntity.ok(parkingService.getPublicAvailabilityByLevel(vehicleType));
    }

    @PostMapping("/assign")
    public ResponseEntity<ParkingAssignmentResponse> assignParkingSpace(@RequestBody ParkingAssignmentRequest request) {
        return ResponseEntity.ok(parkingService.assignParkingSpace(request));
    }

    @PostMapping("/unlock")
    public ResponseEntity<ParkingUnlockResponse> unlockParkingSpace(@RequestBody ParkingUnlockRequest request) {
        return ResponseEntity.ok(parkingService.unlockParkingSpace(request));
    }
}