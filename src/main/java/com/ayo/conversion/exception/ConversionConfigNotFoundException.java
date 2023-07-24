package com.ayo.conversion.exception;

/**
 * This exception will be thrown when the requested conversion is not available
 * in the repo.
 *
 */
public class ConversionConfigNotFoundException extends RuntimeException {

    public ConversionConfigNotFoundException(String fromUnit, String toUnit) {
        super("Conversion from " + fromUnit + " to " + toUnit + " not found");
    }

}
