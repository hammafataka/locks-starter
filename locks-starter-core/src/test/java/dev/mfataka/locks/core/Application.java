package dev.mfataka.locks.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 28.04.2025 16:15
 */

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = "dev.mfataka.locks.*")
public class Application {
}
