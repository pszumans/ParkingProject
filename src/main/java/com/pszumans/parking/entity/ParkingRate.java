package com.pszumans.parking.entity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class ParkingRate {
    @Id
    private String driverType;
    private String currencyName;
    @ElementCollection
    private Map<Integer, Double> parkingRates;
    private double factor;

    public ParkingRate(String driverType, String currencyName, Map<Integer, Double> parkingRates, double factor) {
        this.driverType = driverType;
        this.currencyName = currencyName;
        this.parkingRates = parkingRates;
        this.factor = factor;
    }

    public ParkingRate() {
    }

    public Currency countParkingCostByHours(int hours) {
        Currency cost = new Currency(currencyName);
        double lastHourCost = 0;
        for (int i = 1; i <= hours; i++) {
            if (parkingRates.containsKey(i)) {
                lastHourCost = getHourCost(i);
            } else {
                lastHourCost = lastHourCost * factor;
            }
            cost.addValue(lastHourCost);
        }
        return cost;
    }

    private Double getHourCost(int whichHour) {
        return parkingRates.get(whichHour);
    }
}
