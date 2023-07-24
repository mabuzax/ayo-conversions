package com.ayo.conversion.util;

import com.ayo.conversion.model.ConversionConfig;
import com.ayo.conversion.service.ConversionConfigService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ConversionConfigService conversionConfigService;

    public DataLoader(ConversionConfigService conversionConfigService) {
        this.conversionConfigService = conversionConfigService;
    }

    /**
     * This method is run at the start of the application.
     * It initializes the database with default conversions for meters to feet,
     * Celsius to Fahrenheit, and kilometers to miles.
     *
     * @param args command line arguments. This is not used in the method.
     * @throws Exception if an error occurs during the creation of conversions.
     */
    @Override
    public void run(String... args) throws Exception {
        createConversion("meter", "foot", 3.28084);
        createConversion("celsius", "fahrenheit", 1.8, 32.0);
        createConversion("kilometer", "mile", 0.621371);
    }

    /**
     * This method creates a new Conversion config in the DB at application startup
     * where addend is not provided.
     *
     * @param fromUnit      the source unit to convert from
     * @param toUnit        the target unit to convert to
     * @param convertFactor multiplication factor used for the conversion
     */
    private void createConversion(String fromUnit, String toUnit, double convertFactor) {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit(fromUnit);
        conversion.setToUnit(toUnit);
        conversion.setConvertFactor(convertFactor);
        conversionConfigService.saveConversion(conversion);
    }

    /**
     * This method creates a new Conversion config in the DB at application startup,
     * where addend is provided.
     *
     * @param fromUnit      the source unit to convert from
     * @param toUnit        the target unit to convert to
     * @param addend        for conversions that require a linear transformation
     * @param convertFactor multiplication factor used for the conversion
     */
    private void createConversion(String fromUnit, String toUnit, double convertFactor, Double addend) {
        ConversionConfig conversion = new ConversionConfig();
        conversion.setFromUnit(fromUnit);
        conversion.setToUnit(toUnit);
        conversion.setConvertFactor(convertFactor);
        conversion.setAddend(addend);
        conversionConfigService.saveConversion(conversion);
    }
}
