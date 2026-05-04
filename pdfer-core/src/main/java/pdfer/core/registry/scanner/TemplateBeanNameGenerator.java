package pdfer.core.registry.scanner;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.lang.NonNull;
import pdfer.template.PdfTemplateComponent;

import java.util.Objects;

/**
 * Generates the name of a template bean on the basis of the
 * {@link PdfTemplateComponent#name() name} attribute of the
 * {@link PdfTemplateComponent @PdfTemplateComponent} annotation.
 */
@NoArgsConstructor
public class TemplateBeanNameGenerator implements BeanNameGenerator {

    @Override
    @NonNull
    public String generateBeanName(@NonNull BeanDefinition definition, @NonNull BeanDefinitionRegistry registry) {
        try {
            Class<?> clazz = Class.forName(definition.getBeanClassName());
            PdfTemplateComponent annotation = clazz.getAnnotation(PdfTemplateComponent.class);
            return annotation.name();

        } catch (ClassNotFoundException e) {
            // Should never happen, given the scanner filters in place.
            throw new BeanCreationException(Objects.requireNonNull(definition.getBeanClassName()), "Cannot find bean class");
        }
    }
}
