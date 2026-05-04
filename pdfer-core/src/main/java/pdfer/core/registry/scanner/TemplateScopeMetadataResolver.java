package pdfer.core.registry.scanner;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.lang.NonNull;
import pdfer.template.PdfTemplateComponent;

import java.util.Objects;

import static pdfer.template.PdfTemplateComponent.SCOPE_DEFAULT;
import static pdfer.template.PdfTemplateComponent.SCOPE_SINGLETON;

/**
 * Determines the scope of a template bean on the basis of the
 * {@link PdfTemplateComponent#scope() scope} attribute of the
 * {@link PdfTemplateComponent @PdfTemplateComponent} annotation.
 *
 * <p> Default scope is <CODE>prototype</CODE>.
 */
@NoArgsConstructor
public class TemplateScopeMetadataResolver implements ScopeMetadataResolver {

    @Override
    @NonNull
    public ScopeMetadata resolveScopeMetadata(@NonNull BeanDefinition definition) {
        try {
            Class<?> clazz = Class.forName(definition.getBeanClassName());
            return getScopeMetadata(clazz);

        } catch (ClassNotFoundException e) {
            // Should never happen, given the scanner filters in place.
            throw new BeanCreationException(Objects.requireNonNull(definition.getBeanClassName()), "Cannot find bean class");
        }
    }

    private static ScopeMetadata getScopeMetadata(Class<?> clazz) {
        PdfTemplateComponent annotation = clazz.getAnnotation(PdfTemplateComponent.class);
        ScopeMetadata scope = new ScopeMetadata();
        String scopeSpec = annotation.scope();

        if (SCOPE_DEFAULT.equalsIgnoreCase(scopeSpec)) {
            scope.setScopeName(BeanDefinition.SCOPE_PROTOTYPE);

        } else if (SCOPE_SINGLETON.equalsIgnoreCase(scopeSpec)) {
            scope.setScopeName(BeanDefinition.SCOPE_SINGLETON);

        } else {
            scope.setScopeName(scopeSpec);
        }
        return scope;
    }
}
