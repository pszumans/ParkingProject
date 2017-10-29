package com.pszumans.parking.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "exception")
public class ExceptionMappings {
    private String notAvailableCurrency;
    private String notPaidParking;
    private String parkingAlreadyPaid;
    private String parkingMeterNotStarted;
    private String parkingMeterNotStopped;
    private String parkingMeterStartRepeated;
    private String vehicleUnregistered;
}
