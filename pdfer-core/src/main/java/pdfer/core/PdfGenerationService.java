package pdfer.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pdfer.core.exception.PdferException;
import pdfer.core.registry.TemplateRegistryContainer;
import pdfer.template.PdfTemplate;

import static pdfer.template.PdfTemplateComponent.ROOT_REGISTRY;

/**
 * Finds templates in registry, sets payload and generate documents.
 */
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateRegistryContainer registryContainer;


    public byte[] generatePdfDocumentByPath(String templatePath, Object data) {
        String[] split = PdfTemplate.splitFromPath(templatePath);
        return generatePdfDocument(split[0], split[1], data);
    }

    public byte[] generatePdfDocument(String templateName, Object data) {
        return generatePdfDocument(ROOT_REGISTRY, templateName, data);
    }

    private PdfTemplate<?> findTemplate(String group, String name) {
        return registryContainer.findTemplate(group, name);
    }

    private byte[] generatePdfDocument(String group, String templateName, Object payload) {
        PdfTemplate<?> template = findTemplate(group, templateName);

        Class<?> expectedPayloadClass = template.getPayloadClass();
        Class<?> actualPayloadClass = payload.getClass();

        if (!expectedPayloadClass.isAssignableFrom(actualPayloadClass)) {
            throw new PdferException("Error : payload class " + expectedPayloadClass.getName()
                + " does not match actual class " + actualPayloadClass.getName());
        }

        try {
            PdfTemplate.class.getMethod("setPayload", Object.class).invoke(template, payload);

        } catch (ReflectiveOperationException e) {
            throw new PdferException("Error : could not invoke method.", e);
        }

        if (!template.validate()) {
            throw new PdferException("Error : template validation failed.");
        }

        template.generate();
        return template.getContent();
    }
}
