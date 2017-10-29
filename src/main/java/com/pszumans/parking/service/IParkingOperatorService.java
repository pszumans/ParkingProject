package com.pszumans.parking.service;

import com.pszumans.parking.entity.Driver;
import com.pszumans.parking.entity.ParkingStatus;
import com.pszumans.parking.entity.Vehicle;

import java.util.List;

public interface IParkingOperatorService {
    boolean isVehicleOnParking(String registrationNumber);

    List<Driver> getAllDrivers();
    List<Vehicle> getAllVehicles();
    List<ParkingStatus> getAllParkingStatuses();
    Vehicle getVehicleById(String registrationNumber);
}
