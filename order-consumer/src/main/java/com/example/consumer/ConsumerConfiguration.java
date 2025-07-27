// order-consumer/src/main/java/com/example/consumer/ConsumerConfiguration.java
package com.example.consumer;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

public class ConsumerConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

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

    @Valid
    @NotNull
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

    @Valid
    @NotNull
    @JsonProperty("opensearch")
    private OpenSearchFactory opensearch = new OpenSearchFactory();

    public OpenSearchFactory getOpensearch() { return opensearch; }

    public static class OpenSearchFactory {
        @JsonProperty private String host;
        @JsonProperty  private int port;
        @JsonProperty  private String scheme;
        @JsonProperty  private String index;

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        public String getScheme() { return scheme; }
        public void setScheme(String scheme) { this.scheme = scheme; }
        public String getIndex() { return index; }
        public void setIndex(String index) { this.index = index; }
    }
}
