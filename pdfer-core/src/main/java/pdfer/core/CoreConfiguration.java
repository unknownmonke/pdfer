package pdfer.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan
@ConfigurationPropertiesScan(basePackages = "pdfer.core.props")
public class CoreConfiguration {

    public static final String PROFILE_ACTUATOR = "pdfer-actuator";
    public static final String PROFILE_CLI = "pdfer-cli";
}
