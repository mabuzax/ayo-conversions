package com.ayo.conversion.exception;

/**
 * This exception will be thrown when attempting to save a Conversion config
 * that already exists in the repo.
 *
 */
public class ConversionConfigConflictException extends RuntimeException {

    public ConversionConfigConflictException(String fromUnit, String toUnit) {
        super("Conversion from " + fromUnit + " to " + toUnit + " already exists");
    }

}
