package pdfer.core.mail.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "pdfer.mail")
public class PdferMailProperties {

    private final String sendFrom;
    private final String replyTo;
    private final SmtpServer smtp;
}
