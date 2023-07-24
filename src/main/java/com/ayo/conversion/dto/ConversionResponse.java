package com.ayo.conversion.dto;

import org.springframework.http.HttpStatus;

/**
 * This class will be used to return responses to clients.
 *
 */
public class ConversionResponse<T> {
    private HttpStatus status;
    private T result;

    public ConversionResponse(HttpStatus status, T result) {
        this.status = status;
        this.result = result;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }

}
