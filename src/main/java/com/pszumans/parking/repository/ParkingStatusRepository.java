package com.pszumans.parking.repository;

import com.pszumans.parking.entity.ParkingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingStatusRepository extends JpaRepository<ParkingStatus, Long> {
    ParkingStatus findByVehicleRegistrationNumberAndPaidFalse(String registrationNumber);
    boolean existsByVehicleRegistrationNumberAndActiveTrue(String registrationNumber);
    boolean existsByVehicleRegistrationNumberAndPaidFalse(String registrationNumber);
}
