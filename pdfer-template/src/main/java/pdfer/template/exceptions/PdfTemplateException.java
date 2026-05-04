package pdfer.template.exceptions;

public class PdfTemplateException extends RuntimeException {

    public PdfTemplateException() {}

    public PdfTemplateException(String message) {
        super(message);
    }

    public PdfTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfTemplateException(Throwable cause) {
        super(cause);
    }
}
