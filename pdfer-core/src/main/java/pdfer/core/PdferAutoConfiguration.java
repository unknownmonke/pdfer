package pdfer.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import pdfer.core.mail.model.PdferMailProperties;

@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties(PdferMailProperties.class)
public class PdferAutoConfiguration { }
