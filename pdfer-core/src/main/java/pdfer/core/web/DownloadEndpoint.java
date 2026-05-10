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
import pdfer.core.exception.PdferException;
import pdfer.core.web.model.DownloadRequest;

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
public class DownloadEndpoint {

    private final PdfGenerationService service;


    @PostMapping("/${pdfer.web.endpoint.download-uri:download}/{templateId}")
    public ResponseEntity<byte[]> download(@PathVariable String templateId, @RequestBody DownloadRequest request) {

        byte[] pdfBytes = service.generatePdfDocument(templateId, request.getPayload());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + request.getFilename());

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentLength(pdfBytes.length)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(pdfBytes);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ PdferException.class })
    public void handleException(Exception e) {
        log.error("Error during processing.", e);
    }
}
