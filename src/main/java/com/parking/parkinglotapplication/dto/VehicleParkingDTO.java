package com.parking.parkinglotapplication.dto;

import com.parking.parkinglotapplication.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleParkingDTO {
    private String vehicleNumber;
    private VehicleType vehicleType;
    private int level;
}
