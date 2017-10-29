package com.pszumans.parking.exception;

import lombok.Getter;

@Getter
public class ParkingExceptionInfo {

    private final String error;
    private final String message;

    public ParkingExceptionInfo(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
