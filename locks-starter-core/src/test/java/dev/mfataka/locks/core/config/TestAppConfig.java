package dev.mfataka.locks.core.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 28.04.2025 14:55
 */
@TestConfiguration
public class TestAppConfig {

    @Bean
    public LocksCorePropertyConfig locksCorePropertyConfig() {
        return LocksCorePropertyConfig.defaults();
    }


}
