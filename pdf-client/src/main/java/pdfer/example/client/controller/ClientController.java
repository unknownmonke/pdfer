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
import pdfer.core.mail.model.Email;
import pdfer.core.registry.PdferRegistryContainer;
import pdfer.core.web.model.DownloadRequest;
import pdfer.core.web.model.EmailRequest;
import pdfer.core.web.model.GenerationRequest;

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

    @Value("classpath:templates/download.html")
    private Resource downloadHtmlTemplate;

    @Value("classpath:templates/email.html")
    private Resource emailHtmlTemplate;

    @Value("#{'${pdfer.web.endpoint.base-uri}/${pdfer.web.endpoint.download-uri}'}")
    private String downloadUri;

    @Value("#{'${pdfer.web.endpoint.base-uri}/${pdfer.web.endpoint.mail-uri}'}")
    private String emailUri;

    private final PdferRegistryContainer registryContainer;
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;


    @GetMapping(value = "download", produces = MediaType.TEXT_HTML_VALUE)
    public String getDownloadPage() {
        return htmlTemplateAsString(downloadHtmlTemplate, null);
    }

    @GetMapping(value = "email", produces = MediaType.TEXT_HTML_VALUE)
    public String getEmailPage() {
        return htmlTemplateAsString(emailHtmlTemplate, null);
    }

    /**
     * Takes a {@link Resource} object that represents an HTML template and returns a String with the HTML
     * content and any placeholder properly resolved to the corresponding values.
     */
    private String htmlTemplateAsString(Resource resource, String resultMessage) {

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
                .replace("{{ result }}", resultMessage != null ? resultMessage : "")
                .replace("{{ templates }}", templateSelectHtml);

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Converts frontend form parameters into a {@link GenerationRequest} and sends it to the
     * configured endpoint for generation and download.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> processDownloadForm(@RequestParam String filename,
                                                      @RequestParam String templateName,
                                                      @RequestParam String payload) throws JsonProcessingException {

        if (templateName.isEmpty() || payload.isEmpty()) {
            throw new IllegalArgumentException("Template name and payload must be provided.");
        }

        filename = validateFilename(filename, templateName);
        String uri = buildUri(downloadUri, templateName);

        Map<String, Object> data = mapper.readValue(payload, ((Class<Map<String, Object>>)(Class<?>) Map.class));
        GenerationRequest downloadRequest = new DownloadRequest(data, filename);

        return restTemplate.postForEntity(uri, downloadRequest, byte[].class);
    }

    /**
     * Converts frontend form parameters into a {@link EmailRequest} and sends it to the
     * configured endpoint for generation and send as email attachment.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "email", produces = MediaType.TEXT_HTML_VALUE)
    public String processEmailForm(@RequestParam String filename,
                                   @RequestParam String templateName,
                                   @RequestParam String payload,
                                   @RequestParam String destination,
                                   @RequestParam String subject,
                                   @RequestParam String message) throws JsonProcessingException {

        if (templateName.isEmpty() || payload.isEmpty()) {
            throw new IllegalArgumentException("Template name and payload must be provided.");
        }

        if (destination.isEmpty() || subject.isEmpty()) {
            throw new IllegalArgumentException("Email must have a destination and subject.");
        }

        filename = validateFilename(filename, templateName);
        String uri = buildUri(emailUri, templateName);

        Map<String, Object> data = mapper.readValue(payload, ((Class<Map<String, Object>>)(Class<?>) Map.class));

        Email email = new Email(List.of(destination), subject, message, filename, null, null);
        EmailRequest emailRequest = new EmailRequest(data, email);

        restTemplate.postForEntity(uri, emailRequest, byte[].class);

        return htmlTemplateAsString(emailHtmlTemplate,
            "<p class=\"text-success\">Email with PDF was successfully sent to " + destination + ".</p>");
    }


    private String validateFilename(String filename, String templateName) {
        if (filename.isEmpty()) {
            filename = templateName;
        }
        if (!filename.contains(".pdf")) {
            filename += ".pdf";
        }
        return filename;
    }

    private String buildUri(String endpoint, String templateName) {
        String host = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return host + "/" + endpoint + "/" + templateName;
    }
}
