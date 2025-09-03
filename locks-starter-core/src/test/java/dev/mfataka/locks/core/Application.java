package dev.mfataka.locks.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author HAMMA FATAKA
 */

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = "dev.mfataka.locks.*")
public class Application {
}
