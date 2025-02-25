package com.parking.parkinglotapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAvailabilityDTO {
    private int level;
    private int twoWheelerAvailable;
    private int fourWheelerAvailable;
}
