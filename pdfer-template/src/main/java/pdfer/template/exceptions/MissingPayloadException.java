package pdfer.template.exceptions;

public class MissingPayloadException extends PdfTemplateException {

    public MissingPayloadException(String message) {
        super(message);
    }

    public MissingPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
