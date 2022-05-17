package vml1337j.sws.rest.api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vml1337j.sws.rest.utils.EventScraper;
import vml1337j.sws.rest.utils.RacerScraper;
import vml1337j.sws.rest.utils.SwsUtils;

@Configuration
public class SwsConfig {
    @Bean
    public SwsUtils swsUtils() {
        return new SwsUtils(
                new EventScraper(),
                new RacerScraper()
        );
    }
}
