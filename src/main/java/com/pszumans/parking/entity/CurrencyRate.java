package com.pszumans.parking.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class CurrencyRate {

    @Id
    private String name;
    private double exchangeRate;

    public CurrencyRate() {
    }

    public CurrencyRate(String name, double exchangeRate) {
        this.name = name;
        this.exchangeRate = exchangeRate;
    }
}
