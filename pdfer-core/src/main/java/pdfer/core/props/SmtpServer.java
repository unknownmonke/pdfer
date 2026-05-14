package pdfer.core.props;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Properties;

/**
 * SMTP server configuration class.
 *
 * <p> Class instead of record because Spring annotation processor does not work with records when generating metadata for configuration properties,
 * see <a href="https://github.com/spring-projects/spring-boot/pull/29403">this issue</a>.
 */
@Getter
@RequiredArgsConstructor
public class SmtpServer {

    /** SMTP hostname. Usually something like <CODE>smtp.example.com</CODE> or <CODE>mail.example.com</CODE>. */
    private final String host;

    /** SMTP host port. Usually 25 for unencrypted, 465 for SSL and 587 for TLS connections. */
    private final int port;

    /** Username to authenticate to the SMTP host. Usually the full email address, but not always. */
    private final String username;

    /** Password to authenticate to the SMTP host. */
    private final String password;

    /**
     * Free-format set of properties that will be passed to the SMTP connection.
     * Usually used to set the connection encryption method,
     * e.g. by setting <CODE>mail.smtp.starttls.enable</CODE> to <CODE>true</CODE> for TLS connections.
     */
    private final Properties javaMailProperties;


    public String printProperties() {
        return javaMailProperties.toString();
    }
}
