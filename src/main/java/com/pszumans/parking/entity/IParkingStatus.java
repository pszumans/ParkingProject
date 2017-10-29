package com.pszumans.parking.entity;

public interface IParkingStatus {
    void startParkingMeter();
    void stopParkingMeter();
    int countParkingDurationHours();
}
