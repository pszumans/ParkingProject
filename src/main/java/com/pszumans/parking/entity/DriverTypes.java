package com.pszumans.parking.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "driver.type")
public class DriverTypes {
    private String regular;
    private String vip;
}
