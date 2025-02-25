package com.parking.parkinglotapplication.service;

import com.parking.parkinglotapplication.dto.ParkingAssignmentDTO;
import com.parking.parkinglotapplication.dto.ParkingAvailabilityDTO;
import com.parking.parkinglotapplication.dto.ParkingReleaseDTO;
import com.parking.parkinglotapplication.dto.VehicleParkingDTO;
import com.parking.parkinglotapplication.entity.VehicleType;

import java.util.List;
import java.util.Map;

public interface ParkingService {
    List<ParkingAvailabilityDTO> getAvailableSpaces();

    Map<Integer, Boolean> getPublicAvailability(VehicleType vehicleType);

    ParkingAssignmentDTO assignParkingLot(VehicleParkingDTO parkingRequest);

    ParkingReleaseDTO releaseParkingLot(String vehicleNumber, Long lotId);
}