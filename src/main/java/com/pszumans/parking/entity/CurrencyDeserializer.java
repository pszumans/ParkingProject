package com.pszumans.parking.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CurrencyDeserializer extends JsonDeserializer<Currency> {
    @Override
    public Currency deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String currency = jsonParser.getValueAsString();
        double value = Double.valueOf(currency.split(" ")[0]);
        String name = currency.split(" ")[1];
        return new Currency(value, name);
    }
}
