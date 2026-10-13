package com.comedor.backend.infrastructure.adapters.out.persistence.converters;

import com.comedor.backend.infrastructure.segurity.crypto.AesDeterministicCryptoService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Converter
@Component
public class PhoneCryptoConverter implements AttributeConverter<String, String> {
    private static AesDeterministicCryptoService cryptoService;

    @Autowired
    public void setCryptoService(AesDeterministicCryptoService service) {
        PhoneCryptoConverter.cryptoService = service;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return cryptoService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return cryptoService.decrypt(dbData);
    }
}
