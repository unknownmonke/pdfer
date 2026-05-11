package pdfer.core.mail;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pdfer.core.props.PdferMailProperties;
import pdfer.core.props.SmtpServer;

import java.util.Properties;

/**
 * Autoconfiguration class for mail support.
 *
 * <p> Activates when Spring Mail's {@link JavaMailSender} is on the classpath,
 * and <CODE>pdfer.mail.enable = true</CODE>.
 *
 * <p> Mail capability relies on Spring Mail internally.
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.mail.javamail.JavaMailSender")
@ConditionalOnProperty(name = "pdfer.mail.enable", havingValue = "true")
public class PdferMailConfiguration {

    /**
     * Configures an internal {@link JavaMailSender} bean for concrete email sending,
     * and binds properties from <CODE>pdfer.mail.*</CODE> namespace.
     */
    @Bean
    public JavaMailSender javaMailSender(PdferMailProperties mailProperties) {
        SmtpServer smtp = mailProperties.getSmtpServer();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(smtp.getHost());
        mailSender.setPort(smtp.getPort());
        mailSender.setUsername(smtp.getUsername());
        mailSender.setPassword(smtp.getPassword());

        Properties props = mailSender.getJavaMailProperties();

        if (smtp.getJavaMailProperties() != null) {

            props.putAll(smtp.getJavaMailProperties());
            mailSender.setJavaMailProperties(props);
        }
        return mailSender;
    }
}
