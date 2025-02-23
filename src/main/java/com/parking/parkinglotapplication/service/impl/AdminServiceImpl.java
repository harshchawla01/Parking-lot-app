package com.parking.parkinglotapplication.service.impl;

import com.parking.parkinglotapplication.entity.Availability;
import com.parking.parkinglotapplication.entity.ParkingLot;
import com.parking.parkinglotapplication.entity.ParkingSpace;
import com.parking.parkinglotapplication.entity.VehicleType;
import com.parking.parkinglotapplication.repository.ParkingLotRepository;
import com.parking.parkinglotapplication.repository.ParkingSpaceRepository;
import com.parking.parkinglotapplication.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Override
    public int parKingLotAvailability() {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findAll();
        int availableSlots = 0;

        for (ParkingSpace space : parkingSpaces) {
            availableSlots += parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    space.getLevel(), VehicleType.TW, Availability.AVAILABLE);
            availableSlots += parkingLotRepository.countByLevelAndVehicleTypeAndAvailability(
                    space.getLevel(), VehicleType.FW, Availability.AVAILABLE);
        }

        return availableSlots;
    }

    @Override
    public List<ParkingSpace> fetchparkingSpaceDetails() {
        return parkingSpaceRepository.findAll();
    }
}