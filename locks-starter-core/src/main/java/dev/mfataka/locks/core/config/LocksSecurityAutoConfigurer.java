package dev.mfataka.locks.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.condition.LocksEnabledCondition;
import dev.mfataka.locks.api.condition.SecurityEnabledCondition;


/**
 * @author HAMMA FATAKA
 * @project locks-starter
 * @date 24.04.2025 15:38
 */
@Slf4j
@Configuration
@Conditional(LocksEnabledCondition.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LocksSecurityAutoConfigurer {
    private final LocksCorePropertyConfig properties;

    /**
     * configures security for path {@link LocksCorePropertyConfig.EndpointProperties#getBaseUrl()}  with basic auth,
     * using {@link LocksCorePropertyConfig.EndpointProperties#getUsername()}  as username and {@link LocksCorePropertyConfig.EndpointProperties#getPassword()} as password,
     * if {@link LocksCorePropertyConfig.EndpointProperties#isSecure()}  is true then it will secure it, otherwise it will permit everyone to access locks-starter endpoints
     * <strong>Note:</strong> this {@link SecurityFilterChain} has to be executed before application's security
     *
     * @param httpSecurity application's security
     * @param manager      a manager to create locks-starter user on
     * @return {@link SecurityFilterChain} that would be having higher priority than the application which imports this library
     */
    @Order(5)
    @SneakyThrows
    @Bean(name = "locksSecurityFilterChain")
    @Conditional(SecurityEnabledCondition.class)
    public SecurityFilterChain configure(final HttpSecurity httpSecurity, final UserDetailsManager manager) {
        manager.createUser(properties.getEndpoint().asUser());
        final var path = properties.getEndpoint().getBaseUrl() + "/**";
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(path)
                .authorizeHttpRequests(configurer -> {
                    if (!properties.getEndpoint().isSecure()) {
                        log.trace("all locks-starter endpoints are not secured");
                        configurer.requestMatchers(path).permitAll()
                                .anyRequest().authenticated();
                        return;
                    }
                    configurer.requestMatchers(path).hasAuthority(properties.getEndpoint().getUsername())
                            .anyRequest().authenticated();
                    log.trace("all locks-starter endpoints are secured");

                })
                .httpBasic(Customizer.withDefaults())
                .build();

    }


}

