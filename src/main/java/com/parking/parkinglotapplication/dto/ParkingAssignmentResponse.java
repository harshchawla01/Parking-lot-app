package com.parking.parkinglotapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAssignmentResponse {
    private String vehicleType;
    private String vehicleNumber;
    private int level;
    private Long lotId;
    private LocalDateTime lockingTime;
    private String userId;
}