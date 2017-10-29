package com.pszumans.parking.service;

import com.pszumans.parking.entity.Currency;
import com.pszumans.parking.entity.ParkingStatus;

public interface IDriverService {
    ParkingStatus startParkingMeter(String registrationNumber);
    ParkingStatus stopParkingMeter(String registrationNumber);
    Currency countParkingCost(String registrationNumber, String currency);

    ParkingStatus payForParking(String registrationNumber);
}