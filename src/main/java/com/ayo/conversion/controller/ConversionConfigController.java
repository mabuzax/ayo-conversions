package com.ayo.conversion.controller;

import com.ayo.conversion.dto.ConversionResponse;
import com.ayo.conversion.exception.ConversionConfigConflictException;
import com.ayo.conversion.exception.ConversionConfigNotFoundException;
import com.ayo.conversion.model.ConversionConfig;
import static com.ayo.conversion.util.StringUtil.isNullOrEmpty;

import com.ayo.conversion.service.ConversionConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@CrossOrigin
public class ConversionConfigController {
    private static final Logger logger = LoggerFactory.getLogger(ConversionConfigController.class);
    private final ConversionConfigService conversionConfigService;

    public ConversionConfigController(ConversionConfigService conversionConfigService) {
        this.conversionConfigService = conversionConfigService;
    }

    /**
     * This POST method is responsible for creating a new conversion record.
     *
     * It is triggered when a client sends a POST request to the "/convert/new"
     * endpoint.
     *
     * @param conversion a Conversion config to be saved in the repo. The object
     *                   provided by the client should include 'fromUnit', 'toUnit',
     *                   and 'convertFactor' (and optionally 'addend').
     *
     * @return a ResponseEntity containing a ConversionResponse object.
     *         If the operation was unsuccessful due to a conflict, a CONFLICT
     *         status is returned with a message.
     *         If the operation was unsuccessful due to missing parameters, a BAD
     *         REQUEST status is returned with a message.
     */

    @PostMapping("/conversion/new")
    public ResponseEntity<ConversionResponse> addConversion(@RequestBody ConversionConfig conversion) {

        if (conversion == null || isNullOrEmpty(conversion.getFromUnit()) || isNullOrEmpty(conversion.getToUnit())
                || conversion.getConvertFactor() <= 0
                || (conversion.getAddend() != null && conversion.getAddend() <= 0)) {
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.BAD_REQUEST,
                    "Missing or incorrect parameter(s) for conversion create."), HttpStatus.BAD_REQUEST);
        }

        try {
            ConversionConfig savedConversion = conversionConfigService.saveConversion(conversion);
            return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.CREATED, savedConversion),
                    HttpStatus.CREATED);
        } catch (ConversionConfigConflictException e) {
            return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.CONFLICT, e.getMessage()),
                    HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("An error occurred while saving conversion config.", e);
            return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This GET method gets a specified conversion config from the repo.
     *
     * It is triggered when a client sends a GET request to the
     * "/conversion/show/fromUnit/toUnit" endpoint, providing the source unit and
     * target unit to be retrieved as request parameters.
     * The show will work for vice-versa conversion config as well
     *
     * @param fromUnit the fromUnit of the Conversion to be shown.
     * @param toUnit   the toUnit of the Conversion to be shown.
     *
     * @return a Conversion config for the config.
     *         If the conversion config is not found in the repo, a NOT FOUND status
     *         is returned along with a message.
     *         For other exceptions, an INTERNAL SERVER ERROR status is returned.
     */

    @GetMapping("/conversion/show")
    public ResponseEntity<ConversionResponse> retrieveConversion(@RequestParam String fromUnit,
            @RequestParam String toUnit) {

        if (isNullOrEmpty(fromUnit) || isNullOrEmpty(toUnit)) {
            return new ResponseEntity<>(
                    new ConversionResponse(HttpStatus.BAD_REQUEST, "All parameters [fromUnit, toUnit] are mandatory."),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            ConversionConfig conversion = conversionConfigService.retrieveConversion(fromUnit, toUnit);
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.OK, conversion), HttpStatus.OK);
        } catch (ConversionConfigNotFoundException e) {
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.NOT_FOUND, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("An error occurred while retrieving conversion config.", e);
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while removing the conversion"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This GET method gets all conversion configs from the repo.
     *
     * It is triggered when a client sends a GET request to the
     * "/conversion/show/all" endpoint.
     *
     * @return a list of Conversion configs from the repo.
     *         For any exceptions, an INTERNAL SERVER ERROR status is returned.
     */

    @GetMapping("/conversion/show/all")
    public ResponseEntity<ConversionResponse> retrieveAllConversions() {
        try {
            List<ConversionConfig> conversions = conversionConfigService.retrieveAllConversions();
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.OK, conversions), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while retrieving all conversions.", e);
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while retrieving all conversions"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This PUT method is responsible for updating an existing conversion record.
     *
     * It is triggered when a client sends a PUT request to the "/conversion/update"
     * endpoint.
     *
     * @param conversion a Conversion config to be updated in the repo. The object
     *                   provided by the client should include an existing
     *                   reversible
     *                   ['fromUnit', 'toUnit'], the 'convertFactor' (and optionally
     *                   'addend').
     *
     * @return a ResponseEntity containing the updated ConversionResponse object.
     *         If the operation was unsuccessful due to non-existence of Conversion
     *         config, a NOTFOUNF status is returned with a message.
     *         If the operation was unsuccessful due to missing parameters, a BAD
     *         REQUEST status is returned with a message.
     */

    @PutMapping("/conversion/update")
    public ResponseEntity<ConversionResponse> updateConversion(@RequestBody ConversionConfig conversion) {

        if (conversion == null || isNullOrEmpty(conversion.getFromUnit()) || isNullOrEmpty(conversion.getToUnit())
                || conversion.getConvertFactor() <= 0
                || (conversion.getAddend() != null && conversion.getAddend() <= 0)) {
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.BAD_REQUEST,
                    "Missing or incorrect parameter(s) for conversion update."), HttpStatus.BAD_REQUEST);
        }

        try {
            ConversionConfig updatedConversion = conversionConfigService.updateConversion(conversion);
            return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.OK, updatedConversion), HttpStatus.OK);
        } catch (ConversionConfigNotFoundException e) {
            return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.NOT_FOUND, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("An error occurred while updating conversion config.", e);
            return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This DELETE method deletes a specified conversion config from the repo.
     *
     * It is triggered when a client sends a DELETE request to the
     * "/conversion/remove/fromUnit/toUnit" endpoint, providing the source unit and
     * target unit to be deleted as request parameters.
     * The delete will work for vice-versa conversion config as well
     *
     * @param fromUnit the fromUnit of the Conversion to be removed.
     * @param toUnit   the toUnit of the Conversion to be removed.
     *
     * @return a confirmation that the delete was successful.
     *         If the conversion config is not found in the repo, a NOT FOUND status
     *         is returned along with a message.
     *         For other exceptions, an INTERNAL SERVER ERROR status is returned.
     */

    @DeleteMapping("/conversion/remove")
    public ResponseEntity<ConversionResponse> removeConversion(@RequestParam String fromUnit,
            @RequestParam String toUnit) {

        if (isNullOrEmpty(fromUnit) || isNullOrEmpty(toUnit)) {
            return new ResponseEntity<>(
                    new ConversionResponse(HttpStatus.BAD_REQUEST, "All parameters [fromUnit, toUnit] are mandatory."),
                    HttpStatus.BAD_REQUEST);
        }

        try {
            conversionConfigService.deleteConversion(fromUnit, toUnit);
            return new ResponseEntity<>(
                    new ConversionResponse(HttpStatus.OK, "Conversion Config " + fromUnit + "/" + toUnit + " removed"),
                    HttpStatus.OK);
        } catch (ConversionConfigNotFoundException e) {
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.NOT_FOUND, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("An error occurred while removing conversion config.", e);
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while removing the conversion"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This GET method converts a specified value from one unit to another.
     *
     * It is triggered when a client sends a GET request to the "/convert" endpoint,
     * providing the source unit, target unit, and value to be converted as request
     * parameters.
     *
     * @param fromUnit   the unit of the value to be converted.
     * @param toUnit     the unit the value will be converted to.
     * @param inputValue the value to be converted.
     *
     * @return a ResponseEntity containing a ConversionResponse object.
     *         If the fromUnit and toUnit are identical, an OK HTTP status is
     *         returned along with the inputValue unchanged.
     *         If the operation was unsuccessful due to an IllegalArgumentException,
     *         a BAD REQUEST status is returned along with a message.
     *         If the requested conversion is not found in the database, a NOT FOUND
     *         status is returned along with a message.
     *         For other exceptions, an INTERNAL SERVER ERROR status is returned.
     */

    @GetMapping("/conversion/convert")
    public ResponseEntity<ConversionResponse> convert(@RequestParam String fromUnit, @RequestParam String toUnit,
            @RequestParam double inputValue) {
        double result;

        if (isNullOrEmpty(fromUnit) || isNullOrEmpty(toUnit))
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.BAD_REQUEST,
                    "All parameters [fromUnit, toUnit and inputValue] are mandatory."), HttpStatus.BAD_REQUEST);

        if (fromUnit.equals(toUnit))
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.OK, inputValue), HttpStatus.OK);

        try {
            result = conversionConfigService.convert(fromUnit, toUnit, inputValue);
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.OK, result), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.BAD_REQUEST, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (ConversionConfigNotFoundException e) {
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.NOT_FOUND, e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("An error occurred while performing a conversion.", e);
            return new ResponseEntity<>(new ConversionResponse(HttpStatus.INTERNAL_SERVER_ERROR, 0),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
