package pdfer.example.client.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pdfer.core.registry.PdferRegistryContainer;
import pdfer.core.web.GenerationRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("client")
@RequiredArgsConstructor
public class ClientController {

    @Value("classpath:templates/generate.html")
    private Resource generateHtmlTemplate;

    @Value("#{'${pdfer.web.endpoint.base-uri}/${pdfer.web.endpoint.generate-uri}'}")
    private String generateUri;

    private final PdferRegistryContainer registryContainer;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;


    @GetMapping(value = "generate", produces = MediaType.TEXT_HTML_VALUE)
    public String getGenerateForm() {
        return htmlTemplateAsString(generateHtmlTemplate);
    }

    /**
     * Takes a {@link Resource} object that represents an HTML template and returns a String with the HTML
     * content and any placeholder properly resolved to the corresponding values.
     */
    private String htmlTemplateAsString(Resource resource) {

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {

            String htmlTemplate = FileCopyUtils.copyToString(reader);
            StringBuilder templateSelectHtml = new StringBuilder();
            List<String> templatePaths = registryContainer.allTemplates();

            // Fills in template list.
            for (String path : templatePaths) {
                templateSelectHtml.append("<option>");
                templateSelectHtml.append(path);
                templateSelectHtml.append("</option>\n");
            }
            return htmlTemplate
                .replace("{{ templates }}", templateSelectHtml);

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Converts frontend form parameters into a {@link GenerationRequest} and sends it to the
     * configured PDF generation endpoint.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> processGenerateForm(@RequestParam String filename,
                                                      @RequestParam String templateName,
                                                      @RequestParam String payload) throws JsonProcessingException {

        if (templateName.isEmpty() || payload.isEmpty()) {
            throw new IllegalArgumentException("Template name and payload must be provided.");
        }

        String host = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String uri = host + "/" + generateUri + "/" + templateName;

        Map<String, Object> data = mapper.readValue(payload, ((Class<Map<String, Object>>)(Class<?>) Map.class));
        GenerationRequest generationRequest = buildRequest(filename, templateName, data);

        return restTemplate.postForEntity(uri, generationRequest, byte[].class);
    }

    private GenerationRequest buildRequest(String filename, String templateName, Map<String, Object> data) {

        if (filename.isEmpty()) {
            filename = templateName;
        }
        if (!filename.contains(".pdf")) {
            filename += ".pdf";
        }
        return new GenerationRequest(filename, data);
    }
}
