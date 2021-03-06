package org.eugenez.utils.exception;

/**
* @author eugene zadyra
*/
public class AggregationException extends RuntimeException {
    public AggregationException(String message) {
        super(message);
    }

    public AggregationException(String message, Throwable cause) {
        super(message, cause);
    }
}
