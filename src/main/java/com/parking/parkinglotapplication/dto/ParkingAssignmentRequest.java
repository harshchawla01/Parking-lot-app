package com.parking.parkinglotapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAssignmentRequest {
    private String vehicleType; // TW or FW
    private String vehicleNumber;
    private int level;
}