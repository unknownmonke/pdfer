package pdfer.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TemplateNotFoundException extends PdferRegistryException {

    private final String group;
    private final String templateName;
}
