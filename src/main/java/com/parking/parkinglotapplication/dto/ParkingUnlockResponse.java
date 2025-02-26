package com.parking.parkinglotapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingUnlockResponse {
    private String vehicleNumber;
    private Long lotId;
    private LocalDateTime lockingTime;
    private LocalDateTime unlockingTime;
    private int fee;
    private String userId;
}