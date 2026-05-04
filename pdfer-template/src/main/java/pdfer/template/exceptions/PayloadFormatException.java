package pdfer.template.exceptions;

public class PayloadFormatException extends PdfTemplateException {

    public PayloadFormatException(String message) {
        super(message);
    }

    public PayloadFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
