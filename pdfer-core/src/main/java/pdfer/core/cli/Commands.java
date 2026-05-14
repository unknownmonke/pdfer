package pdfer.core.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import pdfer.core.PdfGenerationService;
import pdfer.core.exception.TemplateNotFoundException;
import pdfer.core.registry.PdferRegistryContainer;
import pdfer.template.PdfTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ShellComponent
@ConditionalOnClass(name = "org.springframework.shell.standard.ShellComponent")
@Profile("pdfer-cli")
@RequiredArgsConstructor
public class Commands {

    private final PdfGenerationService service;
    private final PdferRegistryContainer registryContainer;
    private final ObjectMapper mapper;


    @ShellMethod("Returns a list of all templates known to this instance.")
    public List<String> templates() {
        return registryContainer.allTemplates();
    }

    @ShellMethod("Returns information about a specific template.")
    public String template(String name) {
        try {
            return PdfTemplate.templateToString(registryContainer.findTemplate(name));

        } catch (TemplateNotFoundException e) {
            return "Template not found : " + name;
        }
    }

    @SuppressWarnings("unchecked")
    @ShellMethod("Creates a PDF file from a template and JSON string, then stores it on disk.")
    public String generate(String template, String payload, String filename) throws IOException {

        Map<String, Object> data = mapper.readValue(payload, Map.class);

        byte[] pdf = service.generatePdfDocument(template, data);

        try (FileOutputStream os = new FileOutputStream(filename)) {
            os.write(pdf);
        }
        return String.format("Generated PDF to %s", filename);
    }
}
