package pdfer.core.registry;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import pdfer.core.registry.scanner.TemplateBeanNameGenerator;
import pdfer.core.registry.scanner.TemplateExcludeFilter;
import pdfer.core.registry.scanner.TemplateScopeMetadataResolver;
import pdfer.template.PdfTemplateComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static pdfer.template.PdfTemplateComponent.ROOT_REGISTRY;

/**
 * Custom scanner to register PDF templates.
 *
 * <p> Scans the {@link pdfer.templates} package for templates.
 *
 * <p> Scans specified packages for {@link PdfTemplateComponent @PdfTemplateComponent} annotated implementations of
 * {@link pdfer.template.PdfTemplate PdfTemplate}.
 * <ul>
 *     <li> Default filters are disabled to avoid looking for standard stereotype annotations.
 *     <li> Beans not implementing {@link pdfer.template.PdfTemplate PdfTemplate} are excluded through {@link TemplateExcludeFilter}.
 * </ul>
 *
 * <p> <CODE>@ComponentScan</CODE> annotation cannot be used as adding templates to group is not possible.
 * Without grouping, annotation could be used.
 *
 * <p> <i>Ex :</i>
 * <pre> {@code
 * @ComponentScan(
 *      basePackages = BASE_PACKAGE,
 *      useDefaultFilters = false,
 *      excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, value = TemplateExcludeFilter.class),
 *      includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = PdfTemplateComponent.class),
 *      nameGenerator = TemplateBeanNameGenerator.class,
 *      scopeResolver = TemplateScopeMetadataResolver.class
 * )}
 */
@Slf4j
public class TemplateComponentScanner extends ClassPathBeanDefinitionScanner {

    private final AnnotationConfigApplicationContext rootContext;
    private final Map<String, AnnotationConfigApplicationContext> templateRegistries;


    public TemplateComponentScanner() {
        this(new AnnotationConfigApplicationContext()); // Context must be created before call to super.
    }

    private TemplateComponentScanner(AnnotationConfigApplicationContext context) {
        super(context, false);

        log.debug("Creating root template group {}", context);

        rootContext = context;
        rootContext.setId("pdfer-templates");
        rootContext.setAllowBeanDefinitionOverriding(false);

        templateRegistries = new HashMap<>();
        templateRegistries.put(ROOT_REGISTRY, rootContext);

        // Only matches beans with @PdfTemplateComponent annotation.
        addIncludeFilter(new AnnotationTypeFilter(PdfTemplateComponent.class));

        // Excludes beans not implementing PdfTemplate.
        addExcludeFilter(new TemplateExcludeFilter());

        // Sets bean name from @PdfTemplateComponent parameter.
        setBeanNameGenerator(new TemplateBeanNameGenerator());

        // Sets bean scope from @PdfTemplateComponent parameter.
        setScopeMetadataResolver(new TemplateScopeMetadataResolver());
    }


    /**
     * Always ok to register beans with the same name as long as they go in different contexts.
     * If they don't the context will fail registration anyway.
     */
    @Override
    protected boolean checkCandidate(@NonNull String beanName, @NonNull BeanDefinition beanDefinition) throws IllegalStateException {
        return true;
    }

    /**
     * Actual registering of template beans into their groups.
     * Sub-contexts are populated with templates from the same group.
     * If the group does not exist yet, it is created.
     */
    @Override
    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, @NonNull BeanDefinitionRegistry registry) {
        try {
            Class<?> cls = Class.forName(definitionHolder.getBeanDefinition().getBeanClassName());
            PdfTemplateComponent annotation = cls.getAnnotation(PdfTemplateComponent.class);

            String templateGroup = annotation.group().trim();

            AnnotationConfigApplicationContext subcontext = templateRegistries.get(templateGroup);

            if (subcontext == null) {
                subcontext = new AnnotationConfigApplicationContext();
                subcontext.setId(templateGroup);
                subcontext.setParent(rootContext);
                subcontext.setAllowBeanDefinitionOverriding(false);

                log.debug("Creating new template group {}", templateGroup);

                templateRegistries.put(templateGroup, subcontext);
            }
            log.debug("Registering new template for group {} and name {}", templateGroup, definitionHolder.getBeanName());

            super.registerBeanDefinition(definitionHolder, subcontext);

        } catch (ClassNotFoundException e) {
            // Should never happen, given the scanner filters in place.
            throw new BeanCreationException(
                Objects.requireNonNull(definitionHolder.getBeanDefinition().getBeanClassName()),
                "Cannot find bean class");
        }
    }

    Map<String, AnnotationConfigApplicationContext> getTemplateRegistries() {
        return templateRegistries;
    }

    AnnotationConfigApplicationContext getTemplateRegistry(String group) {
        return templateRegistries.get(group);
    }

    AnnotationConfigApplicationContext getRootRegistry() {
        return getTemplateRegistry(ROOT_REGISTRY);
    }
}
