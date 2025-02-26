package com.parking.parkinglotapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingUnlockRequest {
    private String vehicleNumber;
    private Long lotId; // Optional, can be null
}