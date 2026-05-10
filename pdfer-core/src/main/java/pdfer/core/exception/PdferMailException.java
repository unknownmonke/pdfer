package pdfer.core.exception;

public class PdferMailException extends PdferException {

    public PdferMailException() {}

    public PdferMailException(String message) {
        super(message);
    }

    public PdferMailException(Throwable cause) {
        super(cause);
    }

    public PdferMailException(String message, Throwable cause) {
        super(message, cause);
    }
}
