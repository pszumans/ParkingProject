package com.pszumans.parking.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
public class ParkingEarning {
    @Id
    private LocalDate date;
    private Currency totalEarning;

    public ParkingEarning() {
    }

    public ParkingEarning(LocalDate date, Currency totalEarning) {
        this.date = date;
        this.totalEarning = totalEarning;
    }

    public void addEarnings(double parkingCost) {
        totalEarning.addValue(parkingCost);
    }
}
