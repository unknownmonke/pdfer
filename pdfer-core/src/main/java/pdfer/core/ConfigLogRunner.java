package pdfer.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pdfer.core.props.PdferMailProperties;
import pdfer.core.props.PdferWebProperties;

/**
 * Logs pdfer configuration on startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigLogRunner implements CommandLineRunner {

    private final PdferWebProperties webProperties;
    private final PdferMailProperties mailProperties;

    @Override
    public void run(String... args) {
        log.info("Running pdfer with the following configuration :\n{}{}", webProperties.print(), mailProperties.print());
    }
}
