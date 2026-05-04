package pdfer.core.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdfer.core.PdfGenerationService;

/**
 * API endpoint to generate and download PDFs.
 * Can be enabled through properties.
 */
@Slf4j
@RestController
@RequestMapping("${pdfer.web.endpoint.base_uri:pdfer}")
@ConditionalOnWebApplication
@ConditionalOnBean(type = "pdfer.core.PdfGenerationService")
@ConditionalOnProperty(name = "pdfer.web.endpoint.enable", havingValue = "true")
@RequiredArgsConstructor
public class GenerationEndpoint {

    private final PdfGenerationService service;


    @PostMapping("/${pdfer.web.endpoint.generate-uri:generate}/{templateId}")
    public ResponseEntity<byte[]> generate(@PathVariable String templateId, @RequestBody GenerationRequest request) {

        byte[] pdfBytes = service.generatePdfDocument(templateId, request.payload());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + request.filename());

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentLength(pdfBytes.length)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(pdfBytes);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ RuntimeException.class })
    public void handleException(Exception e) {
        log.error("Error during processing.", e);
    }
}
