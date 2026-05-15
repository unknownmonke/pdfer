package pdfer.core.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pdfer.core.props.WebProperties;

import static pdfer.core.CoreConfiguration.PROFILE_ACTUATOR;

@Component
@Endpoint(id="pdfer-web")
@ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.annotation.Endpoint")
@Profile(PROFILE_ACTUATOR)
@RequiredArgsConstructor
public class WebInfoEndpoint {

    private final WebProperties webProperties;

    /**
     * Exposes properties for web capability.
     */
    @ReadOperation
    public WebProperties webInfo() {
        return webProperties;
    }
}
