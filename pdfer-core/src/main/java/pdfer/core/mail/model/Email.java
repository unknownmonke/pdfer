package pdfer.core.mail.model;

import java.util.List;

public record Email(
    List<String> destinations,
    String subject,
    String content,
    String attachmentFilename,
    String sendFrom,
    String replyTo
) { }
