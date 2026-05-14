package pdfer.core.exception;

public class TemplateRegistryException extends PdferException {

    public TemplateRegistryException() {}

    public TemplateRegistryException(String message) {
        super(message);
    }

    public TemplateRegistryException(Throwable cause) {
        super(cause);
    }

    public TemplateRegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateRegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
