package com.ayo.conversion.service;

import com.ayo.conversion.exception.ConversionConfigConflictException;
import com.ayo.conversion.exception.ConversionConfigNotFoundException;
import com.ayo.conversion.model.ConversionConfig;
import com.ayo.conversion.repository.ConversionConfigRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.ayo.conversion.util.StringUtil.isNullOrEmpty;

@Service
public class ConversionConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConversionConfigService.class);
    private final ConversionConfigRepository repository;

    public ConversionConfigService(ConversionConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * This method converts the given input value from the source unit to the target
     * unit.
     *
     * @param fromUnit   the source unit to convert from
     * @param toUnit     the target unit to convert to
     * @param inputValue the value to be converted
     * @return the converted value
     */
    public double convert(String fromUnit, String toUnit, double inputValue) {
        double result;

        if (isNullOrEmpty(fromUnit) || isNullOrEmpty(toUnit) || inputValue <= 0)
            throw new IllegalArgumentException("Incorrect value for parameter(s) for the conversion");

        ConversionConfig conversion = repository.findByFromUnitAndToUnit(fromUnit, toUnit)
                .orElseGet(() -> repository.findByFromUnitAndToUnit(toUnit, fromUnit)
                        .orElseThrow(() -> new ConversionConfigNotFoundException(fromUnit, toUnit)));

        if (fromUnit.equals(conversion.getFromUnit())) {
            result = inputValue * conversion.getConvertFactor();
        } else {
            result = inputValue / conversion.getConvertFactor();
        }

        if (conversion.getAddend() != null) {
            result += conversion.getAddend();
        }

        return result;
    }

    /**
     * This method saves the given conversion config to the repo.
     *
     * @param conversion the Conversion config to be saved/created in the repo
     * @return the saved Conversion config
     * @throws ConversionConfigConflictException if there is already a Conversion
     *                                           config similar to the object to
     *                                           save
     */
    public ConversionConfig saveConversion(ConversionConfig conversion)
            throws ConversionConfigConflictException, IllegalArgumentException {

        if (conversion == null || isNullOrEmpty(conversion.getFromUnit()) || isNullOrEmpty(conversion.getToUnit())
                || conversion.getConvertFactor() <= 0)
            throw new IllegalArgumentException(
                    "Incorrect value for one of the parameters for the Conversion config create");

        boolean existingConversionExists = repository
                .findByFromUnitAndToUnit(conversion.getFromUnit(), conversion.getToUnit()).isPresent() ||
                repository.findByFromUnitAndToUnit(conversion.getToUnit(), conversion.getFromUnit()).isPresent();

        if (existingConversionExists) {
            throw new ConversionConfigConflictException(conversion.getFromUnit(), conversion.getToUnit());
        }
        try {
            return repository.save(conversion);
        } catch (DataAccessException ex) {
            logger.error("Database error occurred", ex);
            throw new DataAccessResourceFailureException(
                    "System error occurred. Please retry or contact administrator");
        }
    }

    /**
     * This method retrieves all the conversion configs in the repo.
     *
     * @return the list of Conversion configs
     */
    public List<ConversionConfig> retrieveAllConversions() {

        return repository.findAll();
    }

    /**
     * This method retrieves the given conversion config in the repo.
     *
     * It checks for the existence of the conversion using fromUnit and toUnit (and
     * in reverse)
     *
     * @param fromUnit the Conversion FROM unit to be retrieved
     * @param toUnit   the Conversion TO unit to be retrieved
     * @return the Conversion config
     * @throws ConversionConfigNotFoundException if there is no Conversion config to
     *                                           retrieve
     */
    public ConversionConfig retrieveConversion(String fromUnit, String toUnit)
            throws ConversionConfigNotFoundException {

        if (isNullOrEmpty(fromUnit) || isNullOrEmpty(toUnit))
            throw new IllegalArgumentException("Incorrect value for parameter(s) for the conversion retrieval");

        ConversionConfig retrievedConversion = repository.findByFromUnitAndToUnit(fromUnit, toUnit)
                .orElseGet(() -> repository.findByFromUnitAndToUnit(toUnit, fromUnit)
                        .orElseThrow(() -> new ConversionConfigNotFoundException(fromUnit, toUnit)));

        return retrievedConversion;
    }

    /**
     * This method updates the given conversion config in the repo.
     *
     * It first checks for the existence of the conversion using fromUnit and toUnit
     * (and in reverse), if found, it updates the returned object
     *
     * @param conversion the Conversion config to be saved/updated in the repo
     * @return the updated Conversion config
     * @throws ConversionConfigNotFoundException if there is no Conversion config to
     *                                           update
     */
    public ConversionConfig updateConversion(ConversionConfig conversion) throws ConversionConfigNotFoundException {

        if (isNullOrEmpty(conversion.getFromUnit()) || isNullOrEmpty(conversion.getToUnit())
                || conversion.getConvertFactor() <= 0)
            throw new IllegalArgumentException(
                    "Incorrect value supplied for one of the parameters for Conversion config update");

        ConversionConfig existingConversion = repository
                .findByFromUnitAndToUnit(conversion.getFromUnit(), conversion.getToUnit())
                .orElseGet(() -> repository.findByFromUnitAndToUnit(conversion.getToUnit(), conversion.getFromUnit())
                        .orElseThrow(() -> new ConversionConfigNotFoundException(conversion.getFromUnit(),
                                conversion.getToUnit())));

        existingConversion.setConvertFactor(conversion.getConvertFactor());
        existingConversion.setAddend(conversion.getAddend());

        try {
            return repository.save(existingConversion);
        } catch (DataAccessException ex) {
            logger.error("Database error occurred", ex);
            throw new DataAccessResourceFailureException(
                    "System error occurred. Please retry or contact administrator");
        }
    }

    /**
     * This method deletes the given conversion config from the repo.
     *
     * It first checks for the existence of the conversion using fromUnit and toUnit
     * (and in reverse), if found, it deletes the returned object
     *
     * @param fromUnit the Conversion FROM unit to be deleted from the repo
     * @param toUnit   the Conversion TO unit to be deleted from the repo
     * @throws ConversionConfigNotFoundException if there is no Conversion config to
     *                                           delete
     */
    public void deleteConversion(String fromUnit, String toUnit) throws ConversionConfigNotFoundException {

        if (isNullOrEmpty(fromUnit) || isNullOrEmpty(toUnit))
            throw new IllegalArgumentException("Missing parameter(s) for Conversion config removal");

        ConversionConfig existingConversion = repository.findByFromUnitAndToUnit(fromUnit, toUnit)
                .orElseGet(() -> repository.findByFromUnitAndToUnit(toUnit, fromUnit)
                        .orElseThrow(() -> new ConversionConfigNotFoundException(fromUnit, toUnit)));

        repository.delete(existingConversion);
    }

}