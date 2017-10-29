package com.pszumans.parking;

import com.pszumans.parking.entity.*;
import com.pszumans.parking.repository.CurrencyRateRepository;
import com.pszumans.parking.repository.DriverRepository;
import com.pszumans.parking.repository.ParkingRateRepository;
import com.pszumans.parking.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingRateRepository parkingRateRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private final DriverTypes driverTypes;
    private final CurrencyNames currencyNames;

    @Autowired
    public ApplicationStartupRunner(DriverRepository driverRepository, VehicleRepository vehicleRepository,
                                    ParkingRateRepository parkingRateRepository, CurrencyRateRepository currencyRateRepository,
                                    DriverTypes driverTypes, CurrencyNames currencyNames) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingRateRepository = parkingRateRepository;
        this.currencyRateRepository = currencyRateRepository;
        this.driverTypes = driverTypes;
        this.currencyNames = currencyNames;
    }

    @Override
    public void run(String... strings) throws Exception {
        for (int j = 0; j < 3; j++) {
            Driver driver = driverRepository.save(new Driver(driverTypes.getRegular(), "Driver" + j));
            vehicleRepository.save(new Vehicle(driver,"AA0000" + j));
        }

        Driver driver = driverRepository.save(new Driver(driverTypes.getVip(), "DriverVIP"));
        vehicleRepository.save(new Vehicle(driver,"VV00000"));

        Map<Integer, Double> regularRates = new HashMap<>();
        regularRates.put(1,1.0);
        regularRates.put(2,2.0);
        double regularFactor = 2;
        Map<Integer, Double> vipRates = new HashMap<>();
        vipRates.put(1,0.0);
        vipRates.put(2,2.0);
        double vipFactor = 1.5;

        parkingRateRepository.save(new ParkingRate(driverTypes.getRegular(), currencyNames.getPln(), regularRates, regularFactor));
        parkingRateRepository.save(new ParkingRate(driverTypes.getVip(), currencyNames.getPln(), vipRates, vipFactor));

        currencyRateRepository.save(new CurrencyRate(currencyNames.getEur(), 0.25));
        currencyRateRepository.save(new CurrencyRate(currencyNames.getPln(), 1));
        currencyRateRepository.save(new CurrencyRate(currencyNames.getUsd(), 0.3));
    }
}
