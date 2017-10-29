package com.pszumans.parking.controller;

import com.google.common.collect.ImmutableMap;
import com.pszumans.parking.entity.*;
import com.pszumans.parking.service.DriverService;
import com.pszumans.parking.service.ParkingOperatorService;
import com.pszumans.parking.service.ParkingOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ParkingController {

    private final DriverService driverService;
    private final ParkingOperatorService parkingOperatorService;
    private final ParkingOwnerService parkingOwnerService;
    private final CurrencyNames currencyNames;

    @Autowired
    public ParkingController(DriverService driverService, ParkingOperatorService parkingOperatorService, ParkingOwnerService parkingOwnerService,
                             CurrencyNames currencyNames) {
        this.driverService = driverService;
        this.parkingOperatorService = parkingOperatorService;
        this.parkingOwnerService = parkingOwnerService;
        this.currencyNames = currencyNames;
    }

    @GetMapping("/operator/drivers")
    ResponseEntity<List<Driver>> drivers() {
        List<Driver> drivers = parkingOperatorService.getAllDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @GetMapping("/operator/vehicles")
    ResponseEntity<List<Vehicle>> vehicles() {
        List<Vehicle> vehicles = parkingOperatorService.getAllVehicles();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/operator/parkingStatuses")
    ResponseEntity<List<ParkingStatus>> parkingStatuses() {
        List<ParkingStatus> parkingStatuses = parkingOperatorService.getAllParkingStatuses();
        return new ResponseEntity<>(parkingStatuses, HttpStatus.OK);
    }

    @GetMapping("/driver/vehicles/{vehicleId}/status")
    ResponseEntity<ParkingStatus> getParkingStatus(@PathVariable String vehicleId) {
        return new ResponseEntity<>(parkingOperatorService.getParkingStatusByVehicleId(vehicleId), HttpStatus.OK);
    }

    @GetMapping("/driver/vehicles/{vehicleId}/start")
    ResponseEntity<ParkingStatus> startParking(@PathVariable String vehicleId) {
        return new ResponseEntity<>(driverService.startParkingMeter(vehicleId), HttpStatus.OK);
    }

    @GetMapping("/driver/vehicles/{vehicleId}/stop")
    ResponseEntity<ParkingStatus> stopParking(@PathVariable String vehicleId) {
        return new ResponseEntity<>(driverService.stopParkingMeter(vehicleId), HttpStatus.OK);
    }

    @GetMapping("/driver/vehicles/{vehicleId}/count")
    ResponseEntity<?> countParking(@PathVariable String vehicleId) {
        String currency = currencyNames.getDef();
        return countParking(vehicleId, currency);
    }

    @GetMapping("/driver/vehicles/{vehicleId}/count/{currency}")
    ResponseEntity<?> countParking(@PathVariable String vehicleId, @PathVariable String currency) {
        Currency parkingCostByNow = driverService.countParkingCost(vehicleId, currency);
        return new ResponseEntity<>(
                ImmutableMap.of("parkingCostByNow", parkingCostByNow)
                , HttpStatus.OK);
    }

    @GetMapping("/driver/vehicles/{vehicleId}/pay")
    ResponseEntity<ParkingStatus> payForParking(@PathVariable String vehicleId) {
        return new ResponseEntity<>(driverService.payForParking(vehicleId), HttpStatus.OK);
    }

    @GetMapping("/driver/vehicles/{vehicleId}/check")
    ResponseEntity<?> checkVehicle(@PathVariable String vehicleId) {
        boolean vehicleStatus = parkingOperatorService.isVehicleOnParking(vehicleId);
        return new ResponseEntity<>(
                ImmutableMap.of("registrationNumber", vehicleId, "isVehicleOnParking", vehicleStatus)
                , HttpStatus.OK);
    }

    @GetMapping("/owner/earning")
    ResponseEntity<?> checkMoney() {
        LocalDate date = LocalDate.now();
        return checkMoneyByDay(date);
    }

    @GetMapping("/owner/earning/{date}")
    ResponseEntity<?> checkMoneyByDay(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(parkingOwnerService.getEarning(date), HttpStatus.OK);
    }
}
