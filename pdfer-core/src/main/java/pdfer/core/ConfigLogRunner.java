package pdfer.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pdfer.core.props.MailProperties;
import pdfer.core.props.WebProperties;

/**
 * Logs pdfer configuration on startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigLogRunner implements CommandLineRunner {

    private final WebProperties webProperties;
    private final MailProperties mailProperties;

    @Override
    public void run(String... args) {
        log.info("Running pdfer with the following configuration :\n{}{}", webProperties.print(), mailProperties.print());
    }
}
