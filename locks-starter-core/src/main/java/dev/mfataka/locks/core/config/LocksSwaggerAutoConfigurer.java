package dev.mfataka.locks.core.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import dev.mfataka.locks.api.Locker;
import dev.mfataka.locks.api.condition.LocksEnabledCondition;
import dev.mfataka.locks.api.context.LockContext;

/**
 * @author HAMMA FATAKA
 * @project transport-locks-starter
 * @date 05.09.2023 16:05
 */
@Slf4j
@Configuration
@Conditional(LocksEnabledCondition.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureAfter(MultipleOpenApiSupportConfiguration.class)
public class LocksSwaggerAutoConfigurer {


    /**
     * configures {@link GroupedOpenApi} for locks-starter endpoints so that it can be displayed in swagger UI
     *
     * @return {@link GroupedOpenApi} that hold locks-starter endpoints
     */
    @Bean
    public GroupedOpenApi locksStarterEndpoints(final LocksCorePropertyConfig propertyConfig) {
        return GroupedOpenApi.builder()
                .group("locks")
                .packagesToScan("dev.mfataka.locks")
                .pathsToMatch(propertyConfig.getEndpoint().getBaseUrl() + "/**")
                .addOpenApiCustomizer(customizer())
                .build();
    }

    /**
     * configures {@link OpenAPI} in only condition if there is no existing configured {@link OpenAPI},
     * it will add authorize button to locks-starter endpoints
     *
     * @return {@link OpenAPI}
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI locksStarterSecurityDescription() {
        log.trace("Open api config does not exist in this project, will configure it, and apply locksStarter setting for it");
        return cusmoizeOpenApi(new OpenAPI());
    }

    /**
     * configures {@link OpenAPI} when there is existing configured {@link OpenAPI} in the app
     * it will add authorize button to locks-starter endpoints
     *
     * @return {@link OpenApiCustomizer } to be applied
     */

    @Bean
    @ConditionalOnBean(OpenAPI.class)
    public OpenApiCustomizer customizer() {
        log.trace("Open api config already exist in this project, will apply locks-starter setting for it");
        return this::cusmoizeOpenApi;
    }

    private OpenAPI cusmoizeOpenApi(final OpenAPI openApi) {
        final var basicAuthAndFormat = "basic";
        final var lockRole = "Locks Role";
        return openApi.addSecurityItem(new SecurityRequirement()
                        .addList(lockRole))
                .components(new Components()
                        .addSchemas("LockContext", ModelConverters.getInstance().readAllAsResolvedSchema(LockContext.class).schema)
                        .addSchemas("Locker", ModelConverters.getInstance().readAllAsResolvedSchema(Locker.class).schema)
                        .addSecuritySchemes(lockRole, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(basicAuthAndFormat))
                );
    }
}
