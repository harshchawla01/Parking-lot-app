package com.parking.parkinglotapplication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(nullable=false)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.EAGER)
    private ParkingLot parkingLot;

    @Column(nullable=false)
    private int level;

    @Column(nullable=false)
//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inTime;

//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime outTime;

    private int fee;
}