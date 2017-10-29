package com.pszumans.parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
//@JsonSerialize(using = CurrencySerializer.class)
//@JsonDeserialize(using = CurrencyDeserializer.class)
public class Currency implements Serializable {

    public static String DEFAULT;

    @JsonIgnore
    private double value;
    private String name;

    public Currency(double value, String name) {
        this.value = value;
        this.name = name;
    }

    public Currency(String name) {
        this.value = 0;
        this.name = name;
    }

    public void addValue(double value) {
        this.value += value;
    }

    public double getCurrencyValue() {
        return Math.round(value * 100.0) / 100.0;
    }

    @JsonProperty("currencyName")
    public String getName() {
        return name;
    }
}
