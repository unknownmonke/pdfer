package pdfer.core.props;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "pdfer.web.endpoint")
public class WebProperties {

    /** Whether to expose web controller or not. If false, endpoint will not be available. */
    private final boolean enable;

    /** Base URI at which the web controller for the pdfer endpoints will be registered. */
    private final String baseUri;

    /** URI for direct download endpoint. */
    private final String downloadUri;

    /** URI for email endpoint. */
    private final String mailUri;


    public String print() {
        return """
            pdfer.web.endpoint.enable: %s
            pdfer.web.endpoint.base-uri: %s
            pdfer.web.endpoint.download-uri: %s
            pdfer.web.endpoint.mail-uri: %s
        """.formatted(enable, baseUri, downloadUri, mailUri);
    }
}
