package com.pszumans.parking.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class ParkingStatus implements IParkingStatus {

    @Id
    @GeneratedValue
    private Long parkingStatusId;
    @OneToOne
    private Vehicle vehicle;
    private LocalDateTime startParkingTime;
    private LocalDateTime stopParkingTime;
    private int parkingDurationHours;
    private boolean active;
    private boolean paid;
    private Currency parkingCost;

    public ParkingStatus(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public void startParkingMeter() {
        if (startParkingTime == null) {
            startParkingTime = now();
            active = true;
        }
        countParkingDurationHours(startParkingTime);
    }

    @Override
    public void stopParkingMeter() {
        if (stopParkingTime == null) {
            stopParkingTime = now();
            active = false;
        }
        countParkingDurationHours(stopParkingTime);
    }

    @Override
    public int countParkingDurationHours() {
        return countParkingDurationHours(now());
    }

    private int countParkingDurationHours(LocalDateTime time) {
        return parkingDurationHours = countHours(time);
    }

    private int countHours(LocalDateTime time) {
        return (int) Duration.between(startParkingTime, time).toHours() + 1;
    }

    public void payForParking() {
        this.paid = true;
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
