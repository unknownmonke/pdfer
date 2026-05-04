package pdfer.core.registry.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.NonNull;
import pdfer.template.PdfTemplate;

/**
 * Component scanning custom exclude filter.
 * Excludes beans not being of type {@link PdfTemplate PdfTemplate}.
 */
@Slf4j
public class TemplateExcludeFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader reader, @NonNull MetadataReaderFactory factory) {
        try {
            String className = reader.getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(className);
            boolean isExcluded = !PdfTemplate.class.isAssignableFrom(clazz);

            log.debug("Class {} is {} a valid PdfTemplate implementation", className, isExcluded ? "NOT " : "");
            return isExcluded;

        } catch (ClassNotFoundException e) {
            log.error("PdfTemplate class could not be loaded: {}", e.getMessage());
            return false;
        }
    }
}
