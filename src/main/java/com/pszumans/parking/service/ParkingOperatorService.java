package com.pszumans.parking.service;

import com.pszumans.parking.entity.Driver;
import com.pszumans.parking.entity.ExceptionMappings;
import com.pszumans.parking.entity.ParkingStatus;
import com.pszumans.parking.entity.Vehicle;
import com.pszumans.parking.exception.ParkingMeterNotStartedException;
import com.pszumans.parking.exception.VehicleUnregisteredException;
import com.pszumans.parking.repository.DriverRepository;
import com.pszumans.parking.repository.ParkingStatusRepository;
import com.pszumans.parking.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ParkingOperatorService implements IParkingOperatorService {

    private final ParkingStatusRepository parkingStatusRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ExceptionMappings exceptionMappings;

    @Autowired
    public ParkingOperatorService(ParkingStatusRepository parkingStatusRepository, DriverRepository driverRepository, VehicleRepository vehicleRepository, ExceptionMappings exceptionMappings) {
        this.parkingStatusRepository = parkingStatusRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.exceptionMappings = exceptionMappings;
    }

    @Override
    public boolean isVehicleOnParking(String registrationNumber) {
        log.info("Checking if " + registrationNumber + " is on parking");
        return parkingStatusRepository.existsByVehicleRegistrationNumberAndActiveTrue(registrationNumber);
    }

    public boolean isVehicleOnParkingNotPaid(String registrationNumber) {
        return parkingStatusRepository.existsByVehicleRegistrationNumberAndPaidFalse(registrationNumber);
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<ParkingStatus> getAllParkingStatuses() {
        return parkingStatusRepository.findAll();
    }

    public ParkingStatus saveParkingStatus(ParkingStatus parkingStatus) {
        return parkingStatusRepository.save(parkingStatus);
    }

    public ParkingStatus getParkingStatusByVehicleId(String registrationNumber) {
        ParkingStatus parkingStatus = parkingStatusRepository.findByVehicleRegistrationNumberAndPaidFalse(registrationNumber);
        if (parkingStatus == null)
            throw new ParkingMeterNotStartedException(exceptionMappings.getParkingMeterNotStarted());
        return parkingStatus;
    }

    public Vehicle getVehicleById(String registrationNumber) {
        Vehicle vehicle = vehicleRepository.findOne(registrationNumber);
        if (vehicle == null)
            throw new VehicleUnregisteredException(exceptionMappings.getVehicleUnregistered());
        return vehicle;
    }
}
