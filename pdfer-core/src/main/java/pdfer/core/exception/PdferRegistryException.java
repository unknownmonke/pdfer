package pdfer.core.exception;

public class PdferRegistryException extends PdferException {

    public PdferRegistryException() {}

    public PdferRegistryException(String message) {
        super(message);
    }

    public PdferRegistryException(Throwable cause) {
        super(cause);
    }

    public PdferRegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdferRegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
