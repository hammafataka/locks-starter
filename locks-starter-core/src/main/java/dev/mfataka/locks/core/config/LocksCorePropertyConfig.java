package dev.mfataka.locks.core.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 13.03.2023 11:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "locks.starter")
@EnableConfigurationProperties(LocksCorePropertyConfig.class)
public class LocksCorePropertyConfig {
    private boolean enabled;
    private boolean debugEnabled;
    private Duration maxAge;
    private Duration cleanupInterval;
    private EndpointProperties endpoint;

    public static LocksCorePropertyConfig defaults() {
        return new LocksCorePropertyConfig(true, true, Duration.ofMinutes(5), Duration.ofMinutes(4), EndpointProperties.defaults());
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointProperties {
        private String baseUrl;
        private boolean secure;
        private String username;
        private String password;


        public User asUser() {
            return new User(username, password, List.of(new SimpleGrantedAuthority(username)));
        }

        public static EndpointProperties defaults() {
            return new EndpointProperties("/locks", true, "lock", "$2a$12$PuLr16VIffaa/IKTHD0jAOeegZJVKYuWCAiTU8evXjvs38HEx3ml.");
        }
    }
}
