package com.pszumans.parking;


import com.pszumans.parking.entity.Driver;
import com.pszumans.parking.entity.ExceptionMappings;
import com.pszumans.parking.entity.ParkingStatus;
import com.pszumans.parking.entity.Vehicle;
import com.pszumans.parking.repository.DriverRepository;
import com.pszumans.parking.repository.ParkingEarningsRepository;
import com.pszumans.parking.repository.ParkingStatusRepository;
import com.pszumans.parking.repository.VehicleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ParkingControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ParkingStatusRepository parkingStatusRepository;
    @Autowired
    private ParkingEarningsRepository parkingEarningsRepository;
    @Autowired
    private ExceptionMappings exceptionMappings;

    private List<Driver> drivers = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();

    private String registrationNumber = "AA00000";
    private String registrationNumberVip = "VV00000";

    private ParkingStatus parkingStatus;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.parkingStatusRepository.deleteAllInBatch();
        this.vehicleRepository.deleteAllInBatch();
        this.driverRepository.deleteAllInBatch();
        this.parkingEarningsRepository.deleteAllInBatch();

        Driver driver;
        Vehicle vehicle;

        for (int i = 0; i < 3; i++) {
            driver = driverRepository.save(new Driver("REGULAR", "Driver" + i));
            vehicle = vehicleRepository.save(new Vehicle(driver, "AA0000" + i));
            this.drivers.add(driver);
            this.vehicles.add(vehicle);
        }

        driver = driverRepository.save(new Driver("VIP", "Driver3"));
        vehicle = vehicleRepository.save(new Vehicle(driver,"VV00000"));
        this.drivers.add(driver);
        this.vehicles.add(vehicle);
    }

    @Test
    public void testStartingParkingMeter() throws Exception {
        ResultActions result = startParking(registrationNumber);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.registrationNumber", is(registrationNumber)))
                .andExpect(jsonPath("$.startParkingTime", notNullValue()))
                .andExpect(jsonPath("$.parkingDurationHours", greaterThan(0)));

    }

    @Test
    public void testWrongStoppingParkingMeter() throws Exception {
        ResultActions result = stopParking(registrationNumber);

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(exceptionMappings.getParkingMeterNotStarted())));
    }

    @Test
    public void testValidStoppingParking() throws Exception {
        startParking(registrationNumber);
        assertValidStoppingParkingMeter(registrationNumber);
    }

    @Test
    public void testWrongCountingParkingCost() throws Exception {
        assertCountingParkingCost(registrationNumber);
    }

    @Test
    public void testValidCountingParkingCost() throws Exception {
        startParking(registrationNumberVip);

        parkingStatus = parkingStatusRepository.findByVehicleRegistrationNumberAndPaidFalse(registrationNumberVip);
        parkingStatus.setStartParkingTime(LocalDateTime.now().minusHours(5).minusMinutes(10));
        parkingStatusRepository.save(parkingStatus);

        double cost = 26.38; // 26,375

        ResultActions result = countParkingCost(registrationNumberVip);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingCostByNow.currencyName", is("PLN")))
                .andExpect(jsonPath("$.parkingCostByNow.currencyValue", is(cost)));

    }

    @Test
    public void testGettingEarningByOwner() throws Exception {
        startParking(registrationNumber);
        stopParking(registrationNumber);
        payForParking(registrationNumber);
        assertGettingEarning();
    }

    @Test
    public void testSecondParkingAfterPayment() throws Exception {
        startParking(registrationNumber);
        stopParking(registrationNumber);
        payForParking(registrationNumber);
        assertStartingParkingMeter(registrationNumber);
    }

    @Test
    public void testSecondParkingWithoutPayment() throws Exception {
        startParking(registrationNumber);
        stopParking(registrationNumber);

        ResultActions result = startParking(registrationNumber);

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("NotPaidParkingException")))
                .andExpect(jsonPath("$.message", is(exceptionMappings.getNotPaidParking())));
    }

    @Test
    public void testCheckingVehicleByOperator() throws Exception {
        startParking(registrationNumber);
        assertValidCheckingVehicle(registrationNumber);
    }

    @Test
    public void testCheckingUnregisteredVehicleByOperator() throws Exception {
        assertInvalidCheckingVehicle(registrationNumber);
    }

    @Test
    public void testValidPayingForParking() throws Exception {
        startParking(registrationNumber);
        stopParking(registrationNumber);
        assertPayingForParking(registrationNumber);
    }

    private ResultActions stopParking(String registrationNumber) throws Exception {
        return mockMvc.perform(get("/driver/vehicles/{vehicleId}/stop", registrationNumber));
    }

    private void assertCountingParkingCost(String registrationNumber) throws Exception {
        ResultActions result = countParkingCost(registrationNumber);

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(exceptionMappings.getParkingMeterNotStarted())));
    }

    private ResultActions countParkingCost(String registrationNumber) throws Exception {
        return mockMvc.perform(get("/driver/vehicles/{vehicleId}/count", registrationNumber));
    }

    private void assertStartingParkingMeter(String registrationNumber) throws Exception {
         ResultActions result = startParking(registrationNumber);

         result.andExpect(jsonPath("$.vehicle.registrationNumber", is(registrationNumber)))
                .andExpect(jsonPath("$.startParkingTime", notNullValue()))
                .andExpect(jsonPath("$.parkingDurationHours", greaterThan(0)));
    }

    private ResultActions startParking(String registrationNumber) throws Exception {
        return mockMvc.perform(get("/driver/vehicles/{vehicleId}/start", registrationNumber));
    }

    private void assertValidStoppingParkingMeter(String registrationNumber) throws Exception {
        ResultActions result = stopParking(registrationNumber);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.registrationNumber", is(registrationNumber)))
                .andExpect(jsonPath("$.startParkingTime", notNullValue()))
                .andExpect(jsonPath("$.stopParkingTime", notNullValue()))
                .andExpect(jsonPath("$.parkingDurationHours", greaterThan(0)))
                .andExpect(jsonPath("$.active", is(false)));
    }

    private ResultActions checkingVehicle(String registrationNumber) throws Exception {
        return mockMvc.perform(get("/driver/vehicles/{vehicleId}/check", registrationNumber));
    }

    private void assertGettingEarning() throws Exception {
        mockMvc.perform(get("/owner/earning/"))
                .andExpect(jsonPath("$.date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.totalEarning.currencyName", is("PLN")))
                .andExpect(jsonPath("$.totalEarning.currencyValue", is(1.0)));
    }

    private void assertValidCheckingVehicle(String registrationNumber) throws Exception {
        ResultActions result = checkingVehicle(registrationNumber);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber", is(registrationNumber)))
                .andExpect(jsonPath("$.isVehicleOnParking", is(true)));
    }

    private void assertInvalidCheckingVehicle(String registrationNumber) throws Exception {
        ResultActions result = checkingVehicle(registrationNumber);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber", is(registrationNumber)))
                .andExpect(jsonPath("$.isVehicleOnParking", is(false)));
    }

    private ResultActions payForParking(String registrationNumber) throws Exception {
        return mockMvc.perform(get("/driver/vehicles/{vehicleId}/pay", registrationNumber));
    }

    private void assertPayingForParking(String registrationNumber) throws Exception {
        ResultActions result = payForParking(registrationNumber);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.registrationNumber", is(registrationNumber)))
                .andExpect(jsonPath("$.startParkingTime", notNullValue()))
                .andExpect(jsonPath("$.stopParkingTime", notNullValue()))
                .andExpect(jsonPath("$.parkingDurationHours", is(1)))
                .andExpect(jsonPath("$.parkingCost.currencyName", is("PLN")))
                .andExpect(jsonPath("$.parkingCost.currencyValue", is(1.0)));
    }
}
