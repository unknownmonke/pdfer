package pdfer.core.mail.model;

import java.util.Properties;

public record SmtpServer(String host, int port, String username, String password, Properties javaMailProperties) { }
