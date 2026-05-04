package pdfer.core.exception;

public class PdferException extends RuntimeException {

    public PdferException() {}

    public PdferException(String message) {
        super(message);
    }

    public PdferException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdferException(Throwable cause) {
        super(cause);
    }

    public PdferException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
