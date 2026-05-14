package pdfer.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TemplateGroupNotFoundException extends TemplateRegistryException {

    private final String group;
}
