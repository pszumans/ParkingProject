package com.pszumans.parking.repository;

import com.pszumans.parking.entity.ParkingRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRateRepository extends JpaRepository<ParkingRate, String> {
}
