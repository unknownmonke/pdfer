package pdfer.core.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pdfer.core.PdfGenerationService;
import pdfer.core.exception.PdferMailException;
import pdfer.core.mail.PdfMailService;
import pdfer.core.web.model.EmailRequest;

/**
 * API endpoint to send mails with PDF attachments though :
 * <ul>
 *     <li> Mailing capability provided by {@link PdfMailService}.
 *     <li> PDF generation provided by {@link PdfGenerationService}.
 * </ul>
 *
 * <p> Requires both mail capability and web capability active.
 */
@Slf4j
@RestController
@RequestMapping("${pdfer.web.endpoint.base_uri:pdfer}")
@ConditionalOnWebApplication
@ConditionalOnBean(type = { "pdfer.core.mail.PdfMailService", "pdfer.core.PdfGenerationService" })
@ConditionalOnProperty(name = "pdfer.web.endpoint.enable", havingValue = "true")
@ConditionalOnProperty(name = "pdfer.mail.enable", havingValue = "true")
@RequiredArgsConstructor
public class MailEndpoint {

    private final PdfGenerationService service;
    private final PdfMailService mailService;


    @PostMapping("/${pdfer.web.endpoint.mail-uri:mail}/{templateId}")
    public void email(@PathVariable String templateId, @RequestBody EmailRequest request) {

        byte[] pdfBytes = service.generatePdfDocument(templateId, request.getPayload());

        mailService.sendMessageWithPdfAttachmentToList(
            request.getEmail().destinations(),
            request.getEmail().subject(),
            request.getEmail().content(),
            pdfBytes,
            request.getEmail().attachmentFilename(),
            request.getEmail().sendFrom(),
            request.getEmail().replyTo()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ PdferMailException.class })
    public void handleMailException(Exception e) {
        log.error("Error during processing.", e);
    }
}
