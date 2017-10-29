package com.pszumans.parking;

import com.pszumans.parking.entity.ParkingRate;
import com.pszumans.parking.repository.ParkingRateRepository;
import com.pszumans.parking.service.ParkingMeterCalculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ParkingMeterCalculatorTest {

    @MockBean
    private ParkingRateRepository parkingRateRepository;
    @Autowired
    private ParkingMeterCalculator parkingMeterCalculator;

    @Test
    public void countRegularDriverParking() {
        String regular = "REGULAR";
        String pln = "PLN";
        Map<Integer, Double> regularRates = new HashMap<>();
        regularRates.put(1, 1.0);
        regularRates.put(2, 2.0);
        double regularFactor = 2;
        given(parkingRateRepository.findOne(regular)).willReturn(new ParkingRate(regular, pln, regularRates, regularFactor));

        double cost = parkingMeterCalculator.countParkingCost(5, regular, pln).getValue();

        double expectedCost = 31;
        assertEquals(expectedCost, cost, 0);
    }

    @Test
    public void countVipDriverParking() {
        String vip = "VIP";
        String pln = "PLN";
        Map<Integer, Double> vipRates = new HashMap<>();
        vipRates.put(1, 0.0);
        vipRates.put(2, 2.0);
        double vipFactor = 1.5;
        given(parkingRateRepository.findOne(vip)).willReturn(new ParkingRate(vip, pln, vipRates, vipFactor));

        double cost = parkingMeterCalculator.countParkingCost(5, vip, pln).getValue();

        double expectedCost = 16.25;
        assertEquals(expectedCost, cost, 0);
    }
}
