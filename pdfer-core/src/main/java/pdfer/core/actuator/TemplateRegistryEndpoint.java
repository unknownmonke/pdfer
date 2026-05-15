package pdfer.core.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pdfer.core.registry.TemplateRegistryContainer;
import pdfer.template.PdfTemplate;
import pdfer.template.PdfTemplateComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pdfer.core.CoreConfiguration.PROFILE_ACTUATOR;

@Component
@Endpoint(id="pdfer-registry")
@ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.annotation.Endpoint")
@Profile(PROFILE_ACTUATOR)
@RequiredArgsConstructor
public class TemplateRegistryEndpoint {

    private final TemplateRegistryContainer registryContainer;

    @ReadOperation
    public List<String> allTemplates() {
        return registryContainer.allTemplates();
    }

    /**
     * Returns details about a specific template identified by the given path.
     */
    @ReadOperation
    public Map<String, Object> template(@Selector String path) {
        PdfTemplate<?> template = registryContainer.findTemplateByPath(path);

        PdfTemplateComponent templateAnnotation = template.getClass().getAnnotation(PdfTemplateComponent.class);

        Map<String, Object> data = new HashMap<>();
        data.put("classname", template.getClass().getName());
        data.put("template-path", path);
        data.put("template-group", templateAnnotation.group());
        data.put("template-name", templateAnnotation.name());
        data.put("template-scope", templateAnnotation.scope());
        return data;
    }
}
