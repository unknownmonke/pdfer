package pdfer.core.registry;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import pdfer.core.exception.TemplateGroupNotFoundException;
import pdfer.core.exception.TemplateNotFoundException;
import pdfer.template.PdfTemplate;
import pdfer.template.PdfTemplateComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static pdfer.template.PdfTemplateComponent.*;

/**
 * Holds a template registry for each registered template group in a separate context.
 * Launches the scan using {@link TemplateComponentScanner},
 * starts contexts and makes templates available through the {@link TemplateRegistryContainer#findTemplate(String)} method.
 */
@Component
public class TemplateRegistryContainer {

    private final Map<String, AnnotationConfigApplicationContext> templateRegistries;


    public TemplateRegistryContainer() {
        TemplateComponentScanner scanner = new TemplateComponentScanner();
        scanner.scan(BASE_PACKAGE);

        templateRegistries = scanner.getTemplateRegistries();
        for (AnnotationConfigApplicationContext context : templateRegistries.values()) {
            context.refresh();
            context.start();
        }
    }

    public PdfTemplate<?> findTemplateByPath(String path) throws TemplateNotFoundException {
        String[] groupName = PdfTemplate.splitFromPath(path);
        return findTemplate(groupName[0], groupName[1]);
    }

    public PdfTemplate<?> findTemplate(String name) throws TemplateNotFoundException {
        return findTemplate(ROOT_REGISTRY, name);
    }

    public PdfTemplate<?> findTemplate(String group, String name) throws TemplateNotFoundException {
        AnnotationConfigApplicationContext context = templateRegistries.get(group.trim());

        if (context == null) throw new TemplateGroupNotFoundException(group);

        try {
            return context.getBean(name.trim(), PdfTemplate.class);

        } catch (NoSuchBeanDefinitionException e) {
            throw new TemplateNotFoundException(group, name);
        }
    }

    /**
     * @return The list of all registered template paths.
     */
    public List<String> allTemplates() {
        return templateRegistries.entrySet()
            .stream()
            .flatMap(entry ->
                Stream.of(entry.getValue().getBeanNamesForAnnotation(PdfTemplateComponent.class))
                .map(s -> PdfTemplate.getPath(entry.getKey(), s))
                .sorted()
            )
            .toList();
    }

    /**
     * @return The list of all registered groups.
     */
    public List<String> allGroups() {
        return templateRegistries.keySet()
            .stream()
            .map(name -> ROOT_REGISTRY.equals(name) ? GROUP_SEPARATOR : name)
            .sorted()
            .toList();
    }

    /**
     * @return The list of registered templates in the root registry (no group or default group).
     */
    public List<String> templatesForRoot() {
        return templatesForGroup(ROOT_REGISTRY);
    }

    /**
     * @return The list of registered templates in the group.
     */
    public List<String> templatesForGroup(String group) {
        List<String> names = new ArrayList<>(List.of(templateRegistries.get(group).getBeanNamesForAnnotation(PdfTemplateComponent.class)));
        Collections.sort(names);
        return names;
    }
}
