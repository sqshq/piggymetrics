package com.piggymetrics.serverless;

import java.util.Map;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParameterStoreEnvironmentTest {
    private final SsmClient ssm = mock(SsmClient.class);
    private final ParameterStoreEnvironment loader = spy(new ParameterStoreEnvironment());
    private final StandardEnvironment environment = new StandardEnvironment();

    private void configure() {
        environment.getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "APPLICATION_CONFIG_PARAMETER", "/piggymetrics/test/account-service",
                "spring.data.mongodb.uri", "${MONGODB_URI}")));
        doReturn(ssm).when(loader).createClient();
    }

    @Test
    void localStartupDoesNotContactAws() {
        loader.postProcessEnvironment(environment, new SpringApplication());
        verify(loader, never()).createClient();
    }

    @Test
    void decryptsAndResolvesConfigurationWithEnvironmentOverrides() {
        configure();
        when(ssm.getParameter(any(Consumer.class))).thenAnswer(call -> {
            GetParameterRequest.Builder request = GetParameterRequest.builder();
            Consumer<GetParameterRequest.Builder> consumer = call.getArgument(0);
            consumer.accept(request);
            assertThat(request.build().name()).isEqualTo("/piggymetrics/test/account-service");
            assertThat(request.build().withDecryption()).isTrue();
            return GetParameterResponse.builder().parameter(Parameter.builder().value(
                    "{\"MONGODB_URI\":\"mongodb://localhost/test\",\"SMTP_HOST\":\"relay\"}").build()).build();
        });
        environment.getPropertySources().replace("systemEnvironment",
                new MapPropertySource("systemEnvironment", Map.of("SMTP_HOST", "override")));
        loader.postProcessEnvironment(environment, new SpringApplication());
        assertThat(environment.getProperty("spring.data.mongodb.uri")).isEqualTo("mongodb://localhost/test");
        assertThat(environment.getProperty("SMTP_HOST")).isEqualTo("override");
        verify(ssm).close();
    }

    @Test
    void malformedConfigurationFailsWithoutExposingValues() {
        configure();
        when(ssm.getParameter(any(Consumer.class))).thenReturn(GetParameterResponse.builder()
                .parameter(Parameter.builder().value("private-value").build()).build());
        assertThatThrownBy(() -> loader.postProcessEnvironment(environment, new SpringApplication()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unable to load application configuration from Parameter Store")
                .hasNoCause();
        verify(ssm).close();
    }

    @Test
    void missingParameterFailsClosed() {
        configure();
        when(ssm.getParameter(any(Consumer.class))).thenThrow(new IllegalStateException("private-value"));
        assertThatThrownBy(() -> loader.postProcessEnvironment(environment, new SpringApplication()))
                .hasMessage("Unable to load application configuration from Parameter Store").hasNoCause();
    }
}
