package com.pszumans.parking;

import com.pszumans.parking.entity.Currency;
import com.pszumans.parking.entity.CurrencyRate;
import com.pszumans.parking.repository.CurrencyRateRepository;
import com.pszumans.parking.service.CurrencyConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class CurrencyConverterTest {

    @MockBean
    private CurrencyRateRepository currencyRateRepository;
    @Autowired
    private CurrencyConverter currencyConverter;

    @Test
    public void convertCurrency() {
        String eur = "EUR";
        String pln = "PLN";
        CurrencyRate eurRate = new CurrencyRate(eur, 0.25);
        CurrencyRate plnRate = new CurrencyRate(pln, 1);
        Currency cost = new Currency(1, eur);
        given(currencyRateRepository.findOne(eur)).willReturn(eurRate);
        given(currencyRateRepository.findOne(pln)).willReturn(plnRate);

        double plnValue = currencyConverter.exchangeToDefaultCurrency(cost, pln).getValue();

        double expectedPlnValue = 4.0;
        assertEquals(expectedPlnValue, plnValue, 0);
    }


}
