package com.ayo.conversion.exception;

import com.ayo.conversion.dto.ConversionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ConversionResponse<String>> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        return new ResponseEntity<>(
                new ConversionResponse<>(HttpStatus.BAD_REQUEST, "Required parameter [" + name + "] is not present."),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionConfigNotFoundException.class)
    public ResponseEntity<ConversionResponse<String>> handleConversionNotFound(ConversionConfigNotFoundException ex) {
        return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConversionConfigConflictException.class)
    public ResponseEntity<ConversionResponse<String>> handleConversionNotFound(ConversionConfigConflictException ex) {
        return new ResponseEntity<>(new ConversionResponse<>(HttpStatus.CONFLICT, ex.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ConversionResponse<String>> handleGenericException(Exception ex) {
        logger.error("Error occurred", ex);
        return new ResponseEntity<>(
                new ConversionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                        "System error occurred. Please retry or contact administrator."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
