package dev.mfataka.locks.core;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import dev.mfataka.locks.core.config.TestAppConfig;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 28.04.2025 16:09
 */
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ContextConfiguration(classes = {TestAppConfig.class})
@TestPropertySource(locations = "classpath:application.yaml")
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
public class AbstractLockTest {
}
