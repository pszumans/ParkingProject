package com.pszumans.parking.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "currency")
public class CurrencyNames {
    private String pln;
    private String eur;
    private String usd;

    private String def;
}
