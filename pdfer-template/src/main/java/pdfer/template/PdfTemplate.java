package pdfer.template;

import static pdfer.template.PdfTemplateComponent.GROUP_SEPARATOR;
import static pdfer.template.PdfTemplateComponent.ROOT_REGISTRY;

/**
 * Base interface for PDF templates.
 * Custom templates must implement this class.
 *
 * <p> Templates must also be annotated with {@link PdfTemplateComponent},
 * which can specify a template name, group and scope (singleton or prototype).
 *
 * <p> Template lifecycle :
 * <ul>
 *     <li> Created in the registry context when generating a document.
 *     <li> PDF payload is set with {@link #setPayload(Object)}.
 *     <li> {@link #validate()} provides custom payload validation.
 *     <li> {@link #generate()} method processes the payload into actual PDF document.
 *     <li> {@link #getContent()} returns document as byte array to the requester.
 * </ul>
 *
 * <p> Templates can be defined within a <i>group</i> (ex : themed templates) or be a root-level template (no group).
 * Group templates will override any matched root-level template.
 */
public interface PdfTemplate<T> {

    /**
     * @return The concatenated path from group and name using <CODE>group + {@link PdfTemplateComponent#GROUP_SEPARATOR} + name</CODE>.
     */
    static String getPath(String group, String name) {
        if (ROOT_REGISTRY.equals(group)) return name;
        else return group + GROUP_SEPARATOR + name;
    }

    /**
     * @return The concatenated path from template using <CODE>group + {@link PdfTemplateComponent#GROUP_SEPARATOR} + name</CODE>.
     */
    static String getPath(PdfTemplate<?> template) {
        PdfTemplateComponent annotation = template.getClass().getAnnotation(PdfTemplateComponent.class);
        return getPath(annotation.group(), annotation.name());
    }

    /**
     * @return An array of 2 strings : [group, name] extracted from the given template path.
     */
    static String[] splitFromPath(String path) {
        int groupSeparatorIdx = path.indexOf(GROUP_SEPARATOR);

        if ( groupSeparatorIdx < 0) { // No group separator = root-level template.
            return new String[] {ROOT_REGISTRY, path};

        } else {
            String group = path.substring(0, groupSeparatorIdx);
            String name = path.substring(groupSeparatorIdx + GROUP_SEPARATOR.length());
            return new String[] {group, name};
        }
    }

    /**
     * Returns a string representation of a template :
     *
     * <p> <CODE>[path] { from = [payload_class], scope = [template_scope] }</CODE>.
     */
    static String templateToString(PdfTemplate<?> template) {
        PdfTemplateComponent annotation = template.getClass().getAnnotation(PdfTemplateComponent.class);
        return String.format(
            "%s { from = %s, scope = %s }",
            getPath(annotation.group(), annotation.name()),
            template.getPayloadClass().getName(),
            annotation.scope()
        );
    }

    Class<T> getPayloadClass();

    T getPayload();

    void setPayload(T payload);

    boolean validate();

    void generate();

    byte[] getContent();
}
