package com.example.producer;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

public class ProducerConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("rabbitmq")
    private RabbitMqConfig rabbitmq = new RabbitMqConfig();

    public RabbitMqConfig getRabbitmq() {
        return rabbitmq;
    }

    public static class RabbitMqConfig {
        @NotNull @JsonProperty public String host;
        @NotNull @JsonProperty public int    port;
        @NotNull @JsonProperty public String username;
        @NotNull @JsonProperty public String password;
        @NotNull @JsonProperty public String exchange;
        @NotNull @JsonProperty public String queue;
    }

    @JsonProperty("cors")
    private CorsConfiguration cors = new CorsConfiguration();

    public CorsConfiguration getCors() {
        return cors;
    }

    public static class CorsConfiguration {
        @NotEmpty
        @JsonProperty
        private List<String> allowedOrigins = Arrays.asList("http://localhost:3000");

        @NotEmpty
        @JsonProperty
        private List<String> allowedMethods = Arrays.asList("GET", "POST", "OPTIONS");

        @NotEmpty
        @JsonProperty
        private List<String> allowedHeaders = Arrays.asList("Content-Type", "Authorization");

        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> origins) { allowedOrigins = origins; }
        public List<String> getAllowedMethods() { return allowedMethods; }
        public void setAllowedMethods(List<String> methods) { allowedMethods = methods; }
        public List<String> getAllowedHeaders() { return allowedHeaders; }
        public void setAllowedHeaders(List<String> headers) { allowedHeaders = headers; }
    }
}
