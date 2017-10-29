package com.pszumans.parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Vehicle {
    @Id
    private String registrationNumber;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private Driver driver;
    @JsonIgnore
    @OneToOne
    private ParkingStatus parkingStatus;

    public Vehicle(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Vehicle(Driver driver, String registrationNumber) {
        this.driver = driver;
        this.registrationNumber = registrationNumber;
    }

    public Vehicle() {

    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "registrationNumber='" + registrationNumber + '\'' +
                ", driver=" + driver.getNickname() +
                '}';
    }
}

