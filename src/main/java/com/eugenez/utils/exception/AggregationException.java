package com.eugenez.utils.exception;

/**
* @author eugene zadyra
*/
public class AggregationException extends Exception {
    public AggregationException(String message) {
        super(message);
    }

    public AggregationException(String message, Throwable cause) {
        super(message, cause);
    }
}
