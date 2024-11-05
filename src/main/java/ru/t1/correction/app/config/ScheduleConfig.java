package ru.t1.correction.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConditionalOnProperty(value = "spring.application.scheduling.enable",
        havingValue = "true", matchIfMissing = true)
@Configuration
@EnableScheduling
public class ScheduleConfig {

}
