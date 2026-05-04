package pdfer.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stereotype annotation to mark a class as a PDF template for the Pdfer registry.
 *
 * <p> PDF templates must reside in the {@link pdfer.templates} package and must implement the
 * {@link PdfTemplate PdfTemplate} interface.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PdfTemplateComponent {

    String SCOPE_DEFAULT = "prototype";
    String SCOPE_SINGLETON = "singleton";

    String ROOT_REGISTRY = "";
    String BASE_PACKAGE = "pdfer.templates";

    /**
     * Separator used for composing a template path from a name and group. This allows any printable character to be
     * used as either the name or the group.
     *
     * <p> Composing and de-composing template paths should always only be done with
     * {@link PdfTemplate#getPath(String, String) getPath} and
     * {@link PdfTemplate#splitFromPath(String) splitFromPath}.
     */
    String GROUP_SEPARATOR = "\u001D/\u001D";

    String group() default ROOT_REGISTRY;
    String name();
    String scope() default SCOPE_DEFAULT;
}
