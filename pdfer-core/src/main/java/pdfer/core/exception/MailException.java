package pdfer.core.exception;

public class MailException extends PdferException {

    public MailException() {}

    public MailException(String message) {
        super(message);
    }

    public MailException(Throwable cause) {
        super(cause);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
