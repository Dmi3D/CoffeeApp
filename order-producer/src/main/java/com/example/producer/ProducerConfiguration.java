package com.example.producer;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
}
