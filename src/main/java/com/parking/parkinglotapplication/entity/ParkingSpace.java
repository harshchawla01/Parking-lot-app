package com.parking.parkinglotapplication.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParkingSpace {
    @Id
    private int id;
    private int level;
    private int twa;
    private int fwa;
    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ParkingLot> parkingLots;
}