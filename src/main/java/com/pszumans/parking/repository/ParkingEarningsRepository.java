package com.pszumans.parking.repository;

import com.pszumans.parking.entity.ParkingEarning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ParkingEarningsRepository extends JpaRepository<ParkingEarning, LocalDate> {
}
