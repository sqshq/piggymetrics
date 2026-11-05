package com.piggymetrics.serverless;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.services.ssm.SsmClient;

/**
 * Loads one service-specific SecureString JSON document at cold start.
 * Nothing is logged from the parameter, including when parsing fails.
 */
public class ParameterStoreEnvironment implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String parameter = environment.getProperty("APPLICATION_CONFIG_PARAMETER");
        if (parameter == null || parameter.isBlank()) {
            return;
        }
        try (SsmClient ssm = createClient()) {
            String json = ssm.getParameter(request -> request.name(parameter).withDecryption(true))
                    .parameter().value();
            Map<String, Object> properties = new ObjectMapper().readValue(json, new TypeReference<>() {});
            environment.getPropertySources().addAfter(
                    org.springframework.core.env.StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                    new MapPropertySource("parameterStore", properties));
        } catch (Exception exception) {
            // Do not propagate SDK/Jackson messages, which may contain configuration values.
            throw new IllegalStateException("Unable to load application configuration from Parameter Store");
        }
    }

    SsmClient createClient() {
        return SsmClient.create();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
