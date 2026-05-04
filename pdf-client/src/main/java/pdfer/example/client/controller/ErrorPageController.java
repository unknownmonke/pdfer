package pdfer.example.client.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Dynamic error page controller that serves a simple HTML page with the error status code and message.
 *
 * <p> Spring automatically redirects errors to this controller.
 */
@RestController
public class ErrorPageController implements ErrorController {

    @Value("classpath:templates/error.html")
    private Resource errorHtmlTemplate;


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        return htmlTemplateAsString(errorHtmlTemplate, request);
    }

    /**
     * Takes a {@link Resource} object that represents an HTML template and returns a String with the HTML
     * content and any placeholder properly resolved to the corresponding values.
     */
    private static String htmlTemplateAsString(Resource resource, HttpServletRequest request) {

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {

            Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
            String message = (String) request.getAttribute("jakarta.servlet.error.message");
            Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");

            return FileCopyUtils.copyToString(reader)
                .replace("{{ status }}", statusCode != null ? statusCode.toString() : "-")
                .replace("{{ message }}", message != null ? message : exception.getMessage());

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
