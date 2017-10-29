package com.pszumans.parking.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@Entity
public class Driver {

    private String driverType;
    @Id
    private String nickname;
    @OneToOne(cascade = CascadeType.ALL)
    private Vehicle vehicle;

    private Driver(String nickname) {
        this.nickname = nickname;
    }

    public Driver(String driverType, String nickname) {
        this.driverType = driverType;
        this.nickname = nickname;
    }

    public Driver(String driverType, String nickname, Vehicle vehicle) {
        this.driverType = driverType;
        this.nickname = nickname;
        this.vehicle = vehicle;
    }

    public Driver() {
    }
}
