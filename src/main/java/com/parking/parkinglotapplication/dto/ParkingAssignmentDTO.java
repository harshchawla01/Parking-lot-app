package com.parking.parkinglotapplication.dto;

import com.parking.parkinglotapplication.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAssignmentDTO {
    private String vehicleNumber;
    private VehicleType vehicleType;
    private int level;
    private Long lotNumber;
    private LocalDateTime lockingTime;
    private String userId;
}
