package pdfer.core.mail;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pdfer.core.mail.model.PdferMailProperties;
import pdfer.core.mail.model.SmtpServer;

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
        SmtpServer smtp = mailProperties.getSmtp();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(smtp.host());
        mailSender.setPort(smtp.port());
        mailSender.setUsername(smtp.username());
        mailSender.setPassword(smtp.password());

        Properties props = mailSender.getJavaMailProperties();

        if (smtp.javaMailProperties() != null) {

            props.putAll(smtp.javaMailProperties());
            mailSender.setJavaMailProperties(props);
        }
        return mailSender;
    }
}
