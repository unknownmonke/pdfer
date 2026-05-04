package pdfer.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pdfer.template.exceptions.MissingPayloadException;
import pdfer.template.exceptions.PayloadFormatException;

import java.util.Map;

/**
 * Abstract generic template to work with JSON payloads.
 *
 * <p> Converts payload to a generic map of key values without the need to deserialize into a specific model class.
 * This class adds abstraction logic over the underlying {@link PdfTemplate} interface to convert JSON payload to Map.
 *
 * <p> Overridden methods are provided as is. Subclasses must still implement other methods,
 * including {@link PdfTemplate#generate() generate} to build PDF object from provided map using internal implementation framework.
 */
public abstract class AbstractJsonPdfTemplate implements PdfTemplate<Map<String, Object>> {

    private Map<String, Object> payloadMap;

    private final ObjectMapper mapper = new ObjectMapper();


    // Exposes sub method for custom logic as interface method is implemented to provide additional logic.
    protected boolean validatePayload() {
        return true;
    }

    // Exposes sub method for custom logic as interface method is implemented to provide additional logic.
    @SuppressWarnings("unchecked")
    public void setJsonPayload(String json) throws PayloadFormatException {
        try {
            setPayload(getMapper().readValue(json, Map.class));

        } catch (IllegalArgumentException | JsonProcessingException e) {
            throw new PayloadFormatException("Cannot convert payload to map.", e);
        }
    }

    protected ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public Map<String, Object> getPayload() {
        return payloadMap;
    }

    @Override
    public void setPayload(Map<String, Object> payloadMap) {
        this.payloadMap = payloadMap;
    }

    @Override
    public boolean validate() throws MissingPayloadException {
        if (payloadMap == null) throw new MissingPayloadException("Payload is null.");
        return validatePayload();
    }
}
