package pdfer.core.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.system.JavaVersion;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pdfer.core.exception.PdferMailException;
import pdfer.core.mail.model.PdferMailProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@ConditionalOnClass(name = "org.springframework.mail.javamail.JavaMailSenderImpl")
@ConditionalOnProperty(name = "pdfer.mail.enable", havingValue = "true")
@ConditionalOnJava(range = ConditionalOnJava.Range.EQUAL_OR_NEWER, value = JavaVersion.SEVENTEEN)
@RequiredArgsConstructor
public class PdfMailService {

    private final JavaMailSender mailSender;
    private final PdferMailProperties mailProperties;

    /**
     * Sends a message with PDF attachment to a single string of email addresses separated by commas.
     * Uses configured properties for sendFrom and replyTo.
     */
    public void sendMessageWithPdfAttachment(String destinations, String subject, String content,
                                             byte[] attachment, String attachmentFilename) throws PdferMailException {
        sendMessageWithPdfAttachmentToList(
            Arrays.asList(destinations.split(",")),
            subject, content, attachment, attachmentFilename,
            null, null);
    }

    /**
     * Sends a message with PDF attachment to a single string of email addresses separated by commas.
     */
    public void sendMessageWithPdfAttachment(String destinations, String subject, String content,
                                             byte[] attachment, String attachmentFilename,
                                             String sendFrom, String replyTo) throws PdferMailException {
        sendMessageWithPdfAttachmentToList(
            Arrays.asList(destinations.split(",")),
            subject, content, attachment, attachmentFilename,
            sendFrom, replyTo);
    }

    /**
     * Sends a message with PDF attachment to a list of email addresses as String.
     */
    public void sendMessageWithPdfAttachmentToList(List<String> destinations, String subject, String content,
                                                   byte[] attachment, String attachmentFilename,
                                                   String sendFrom, String replyTo) throws PdferMailException {

        List<InternetAddress> recipients = destinations
            .stream()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(address -> {
                try {
                    return new InternetAddress(address);

                } catch (AddressException e) {
                    throw new PdferMailException(e);
                }
            })
            .toList();

        sendMessageWithPdfAttachment(recipients, subject, content, attachment, attachmentFilename, sendFrom, replyTo);
    }

    /**
     * Sends a message with PDF attachment to a list of email addresses as {@link InternetAddress}.
     */
    public void sendMessageWithPdfAttachment(List<InternetAddress> destinations, String subject, String content,
                                             byte[] attachment, String attachmentFilename,
                                             String sendFrom, String replyTo) throws PdferMailException {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            if (sendFrom != null || mailProperties.getSendFrom() != null) {
                // Sets from arguments or directly from properties if not specified.
                helper.setFrom(
                    Objects.requireNonNullElseGet(sendFrom,
                        () -> String.format("<%s>", mailProperties.getSendFrom())
                    ));
            }

            if (replyTo != null || mailProperties.getReplyTo() != null) {
                // Sets from arguments or directly from properties if not specified.
                helper.setReplyTo(
                    Objects.requireNonNullElseGet(replyTo,
                        () -> String.format("<%s>", mailProperties.getReplyTo())
                    ));
            }

            helper.setTo(destinations.toArray(new InternetAddress[]{}));
            helper.setSubject(subject);
            helper.setText(content);

            helper.addAttachment(attachmentFilename, new ByteArrayResource(attachment), MediaType.APPLICATION_PDF_VALUE);

        } catch (MessagingException e) {
            throw new PdferMailException(e);
        }

        try {
            mailSender.send(message);

        } catch (MailException e) {
            throw new PdferMailException(e);
        }
    }
}
