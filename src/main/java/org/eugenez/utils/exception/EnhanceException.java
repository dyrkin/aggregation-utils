package org.eugenez.utils.exception;

/**
 * @author eugene zadyra
 */
public class EnhanceException extends RuntimeException{
    public EnhanceException(String message) {
        super(message);
    }

    public EnhanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnhanceException(Throwable cause) {
        super(cause);
    }
}
