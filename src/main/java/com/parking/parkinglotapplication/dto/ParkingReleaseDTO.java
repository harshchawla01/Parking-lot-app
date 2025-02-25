package com.parking.parkinglotapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingReleaseDTO {
    private String vehicleNumber;
    private Long lotNumber;
    private LocalDateTime lockingTime;
    private LocalDateTime unlockingTime;
    private int fee;
    private String userId;
}
