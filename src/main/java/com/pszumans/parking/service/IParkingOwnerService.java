package com.pszumans.parking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pszumans.parking.entity.ParkingEarning;

import java.time.LocalDate;

public interface IParkingOwnerService {
    ParkingEarning getEarning(LocalDate date) throws JsonProcessingException;
}
