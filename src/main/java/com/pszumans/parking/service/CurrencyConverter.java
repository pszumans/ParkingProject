package com.pszumans.parking.service;

import com.pszumans.parking.entity.Currency;
import com.pszumans.parking.entity.CurrencyRate;
import com.pszumans.parking.repository.CurrencyRateRepository;
import com.pszumans.parking.entity.ExceptionMappings;
import com.pszumans.parking.exception.NotAvailableCurrencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConverter {

    private final CurrencyRateRepository currencyRateRepository;
    private final ExceptionMappings exceptionMappings;

    @Autowired
    public CurrencyConverter(CurrencyRateRepository currencyRateRepository, ExceptionMappings exceptionMappings) {
        this.currencyRateRepository = currencyRateRepository;
        this.exceptionMappings = exceptionMappings;
    }

    public Currency exchangeToDefaultCurrency(Currency cost, String currency) {
        CurrencyRate fromCurrencyRate = getCurrencyRate(cost.getName());
        CurrencyRate toCurrencyRate = getCurrencyRate(currency);

        checkCurrencyAvailability(toCurrencyRate);

        double fromExchangeRate = fromCurrencyRate.getExchangeRate();
        double toExchangeRate = toCurrencyRate.getExchangeRate();

        double value = cost.getValue() / fromExchangeRate * toExchangeRate;
        return new Currency(value, currency);
    }

    private void checkCurrencyAvailability(CurrencyRate currencyRate) {
        if (currencyRate == null)
            throw new NotAvailableCurrencyException(exceptionMappings.getNotAvailableCurrency());
    }

    private CurrencyRate getCurrencyRate(String currency) {
        return currencyRateRepository.findOne(currency);
    }

}
