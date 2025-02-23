package com.parking.parkinglotapplication.controller;

import com.parking.parkinglotapplication.entity.ParkingSpace;
import com.parking.parkinglotapplication.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/available-slots")
    public int availableSlots() {
        return adminService.parKingLotAvailability();
    }

    @GetMapping("/parking-spaces")
    public List<ParkingSpace> parkingSpaceInfo() {
        return adminService.fetchparkingSpaceDetails();
    }

}

// gaadi ko lot dena

// unlot krnaa

// gaadi se aadmi dhundna