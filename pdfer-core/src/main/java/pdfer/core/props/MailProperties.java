package pdfer.core.props;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "pdfer.mail")
public class MailProperties {

    /** Whether to expose mail controller, service and mailer bean. If false, none will be available. */
    private final boolean enable;

    /** Sender email address. */
    private final String sendFrom;

    /** Email address where replies should be sent. */
    private final String replyTo;

    /** External SMTP server configuration. */
    @NestedConfigurationProperty
    private final SmtpServer smtpServer;


    public String print() {
        return """
            pdfer.mail.enable: %s
            pdfer.mail.send-from: %s
            pdfer.mail.reply-to: %s
            pdfer.mail.smtp.host: %s
            pdfer.mail.smtp.port: %d
            pdfer.mail.smtp.username: %s
            pdfer.mail.smtp.java-mail-properties: %s
        """.formatted(enable, sendFrom, replyTo,
                smtpServer.getHost(), smtpServer.getPort(), smtpServer.getUsername(), smtpServer.printProperties());
    }
}
