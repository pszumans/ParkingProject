package com.pszumans.parking.service;

import com.pszumans.parking.entity.Currency;
import com.pszumans.parking.entity.CurrencyNames;
import com.pszumans.parking.entity.ParkingEarning;
import com.pszumans.parking.repository.ParkingEarningsRepository;
import com.pszumans.parking.entity.ParkingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class ParkingOwnerService implements IParkingOwnerService {

    private final ParkingEarningsRepository parkingEarningsRepository;
    private final CurrencyNames currencyNames;

    @Autowired
    public ParkingOwnerService(ParkingEarningsRepository parkingEarningsRepository, CurrencyNames currencyNames) {
        this.parkingEarningsRepository = parkingEarningsRepository;
        this.currencyNames = currencyNames;
    }

    @Override
    public ParkingEarning getEarning(LocalDate date) {
        log.info("Getting earning for date: " + date + " by parking owner");
        return getParkingEarningsByDate(date);
    }

    public void getPaymentForParking(ParkingStatus parkingStatus) {
        LocalDate today = parkingStatus.getStopParkingTime().toLocalDate();
        ParkingEarning parkingEarning = parkingEarningsRepository.findOne(today);
        if (parkingEarning == null)
            parkingEarning = new ParkingEarning(today, new Currency(currencyNames.getDef()));
        parkingEarning.addEarnings(parkingStatus.getParkingCost().getValue());
        parkingStatus.payForParking();
        parkingEarningsRepository.save(parkingEarning);
    }

    private ParkingEarning getParkingEarningsByDate(LocalDate date) {
        return parkingEarningsRepository.findOne(date);
    }
}
