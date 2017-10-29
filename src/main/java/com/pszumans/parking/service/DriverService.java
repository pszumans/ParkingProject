package com.pszumans.parking.service;

import com.pszumans.parking.entity.*;
import com.pszumans.parking.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DriverService implements IDriverService {

    private final ParkingOperatorService parkingOperatorService;
    private final ParkingOwnerService parkingOwnerService;
    private final ParkingMeterCalculator parkingMeterCalculator;
    private final ExceptionMappings exceptionMappings;
    private final CurrencyNames currencyNames;

    @Autowired
    public DriverService(ParkingOperatorService parkingOperatorService, ParkingOwnerService parkingOwnerService, ParkingMeterCalculator parkingMeterCalculator, ExceptionMappings exceptionMappings, CurrencyNames currencyNames) {
        this.parkingOperatorService = parkingOperatorService;
        this.parkingOwnerService = parkingOwnerService;
        this.parkingMeterCalculator = parkingMeterCalculator;
        this.exceptionMappings = exceptionMappings;
        this.currencyNames = currencyNames;
    }

    @Override
    public ParkingStatus startParkingMeter(String registrationNumber) {
        log.info("Starting parking meter for " + registrationNumber);
        isVehicleOnParking(registrationNumber);
        Vehicle vehicle = getVehicle(registrationNumber);
        ParkingStatus parkingStatus = new ParkingStatus(vehicle);
        parkingStatus.startParkingMeter();
        saveParkingStatus(parkingStatus);
        return parkingStatus;
    }

    private ParkingStatus getParkingStatusByVehicleId(String registrationNumber) {
        return parkingOperatorService.getParkingStatusByVehicleId(registrationNumber);
    }

    @Override
    public ParkingStatus stopParkingMeter(String registrationNumber) {
        log.info("Stopping parking meter for " + registrationNumber);
        ParkingStatus parkingStatus = getParkingStatusByVehicleId(registrationNumber);
        hasParkingStarted(parkingStatus);
        Currency parkingCost = countParkingCost(parkingStatus, currencyNames.getDef());
        parkingStatus.stopParkingMeter();
        parkingStatus.setParkingCost(parkingCost);
        saveParkingStatus(parkingStatus);
        return parkingStatus;
    }

    @Override
    public Currency countParkingCost(String registrationNumber, String currency) {
        log.info("Counting, how much " + currency + " should be paid for " + registrationNumber + " parking by now");
        ParkingStatus parkingStatus = getParkingStatusByVehicleId(registrationNumber);
        hasParkingStarted(parkingStatus);
        Currency parkingCostByNow = countParkingCost(parkingStatus, currency);
        return parkingCostByNow;
    }

    @Override
    public ParkingStatus payForParking(String registrationNumber) {
        ParkingStatus parkingStatus = getParkingStatusByVehicleId(registrationNumber);
        hasParkingStarted(parkingStatus);
        payCheck(parkingStatus);
        payToOwner(parkingStatus);
        return parkingStatus;
    }

    private Vehicle getVehicle(String registrationNumber) {
        return parkingOperatorService.getVehicleById(registrationNumber);
    }

    private ParkingStatus saveParkingStatus(ParkingStatus parkingStatus) {
        return parkingOperatorService.saveParkingStatus(parkingStatus);
    }

    private void isVehicleOnParking(String registrationNumber) {
        if (parkingOperatorService.isVehicleOnParking(registrationNumber))
            throw new ParkingMeterStartRepeatedException(exceptionMappings.getParkingMeterStartRepeated());
        if (parkingOperatorService.isVehicleOnParkingNotPaid(registrationNumber))
            throw new NotPaidParkingException(exceptionMappings.getNotPaidParking());
    }

    private Currency countParkingCost(ParkingStatus parkingStatus, String currency) {
        int hours = parkingStatus.countParkingDurationHours();
        String driverType = getDriverType(parkingStatus);
        Currency parkingCostByNow = countParkingCost(hours, driverType, currency);
        return parkingCostByNow;
    }

    private String getDriverType(ParkingStatus parkingStatus) {
        return parkingStatus.getVehicle().getDriver().getDriverType();
    }

    private Currency countParkingCost(int hours, String driverType, String currency) {
        return parkingMeterCalculator.countParkingCost(hours, driverType, currency);
    }

    private void hasParkingStarted(ParkingStatus parkingStatus) {
        if (parkingStatus == null || parkingStatus.getStartParkingTime() == null)
            throw new ParkingMeterNotStartedException(exceptionMappings.getParkingMeterNotStarted());
    }

    private void payToOwner(ParkingStatus parkingStatus) {
        parkingOwnerService.getPaymentForParking(parkingStatus);
    }

    private void payCheck(ParkingStatus parkingStatus) {
        if (parkingStatus.isActive())
            throw new ParkingMeterNotStoppedException(exceptionMappings.getParkingMeterNotStopped());
        if (parkingStatus.isPaid())
            throw new ParkingAlreadyPaidException(exceptionMappings.getParkingAlreadyPaid());
    }
}
