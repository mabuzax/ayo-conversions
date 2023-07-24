package com.ayo.conversion.repository;

import com.ayo.conversion.model.ConversionConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversionConfigRepository extends JpaRepository<ConversionConfig, Long> {

    /**
     * This method retrieves a Conversion config for a specified conversion in the
     * repo.
     *
     * @param fromUnit the source unit to convert from
     * @param toUnit   the target unit to convert to
     * @return the Conversion config if it exists
     */
    Optional<ConversionConfig> findByFromUnitAndToUnit(String fromUnit, String toUnit);

}