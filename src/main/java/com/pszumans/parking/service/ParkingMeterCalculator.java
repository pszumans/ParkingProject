package com.pszumans.parking.service;

import com.pszumans.parking.entity.Currency;
import com.pszumans.parking.repository.ParkingRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParkingMeterCalculator {

    private final ParkingRateRepository parkingRateRepository;
    private final CurrencyConverter currencyConverter;

    @Autowired
    public ParkingMeterCalculator(ParkingRateRepository parkingRateRepository, CurrencyConverter currencyConverter) {
        this.parkingRateRepository = parkingRateRepository;
        this.currencyConverter = currencyConverter;
    }

    public Currency countParkingCost(int hours, String driverType, String currency) {
        Currency costBeforeExchange = parkingRateRepository.findOne(driverType).countParkingCostByHours(hours);
        return currencyConverter.exchangeToDefaultCurrency(costBeforeExchange, currency);
    }

}
