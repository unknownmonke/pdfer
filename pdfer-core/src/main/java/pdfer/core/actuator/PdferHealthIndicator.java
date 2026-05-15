package pdfer.core.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pdfer.core.PdfGenerationService;
import pdfer.core.mail.MailService;
import pdfer.core.registry.TemplateRegistryContainer;

import java.util.ArrayList;
import java.util.List;

import static pdfer.core.CoreConfiguration.PROFILE_ACTUATOR;

@Component
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
@Profile(PROFILE_ACTUATOR)
@RequiredArgsConstructor
public class PdferHealthIndicator implements HealthIndicator {

    private final ApplicationContext context;

    @Override
    public Health health() {
        List<String> missing = new ArrayList<>();
        List<String> tooMany = new ArrayList<>();

        String[] service = context.getBeanNamesForType(PdfGenerationService.class);

        if (service.length == 0) {
            missing.add("service");

        } else if (service.length > 1) {
            tooMany.add("service");
        }

        String[] registry = context.getBeanNamesForType(TemplateRegistryContainer.class);

        if (registry.length == 0) {
            missing.add("registry");

        } else if (registry.length > 1) {
            tooMany.add("registry");
        }

        String[] mailer = context.getBeanNamesForType(MailService.class);

        if (mailer.length == 0) {
            missing.add("mailer");

        } else if (mailer.length > 1) {
            tooMany.add("mailer");
        }

        // Exactly 1 bean of each.
        if (missing.isEmpty() && tooMany.isEmpty()) {

            return Health.up()
                .withDetail("service", service[0])
                .withDetail("registry", registry[0])
                .withDetail("mailer", mailer[0])
                .build();

        } else {
            Health.Builder healthBuilder;

            if (!missing.isEmpty()) {
                healthBuilder = Health.down();

            } else {
                healthBuilder = Health.unknown();
            }

            return healthBuilder
                .withDetail("service", missing.contains("service") ? "<none>" : "<many>")
                .withDetail("registry", missing.contains("registry") ? "<none>" : "<many>")
                .withDetail("mailer", missing.contains("mailer") ? "<none>" : "<many>")
                .build();
        }
    }
}
