package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
