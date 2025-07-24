package com.example.producer;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class ProducerApplication extends Application<ProducerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ProducerApplication().run(args);
    }

    @Override
    public String getName() {
        return "Order Producer Service";
    }

    @Override
    public void initialize(final Bootstrap<ProducerConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(ProducerConfiguration cfg, Environment env) throws Exception {
        ProducerConfiguration.CorsConfiguration corsConfig = cfg.getCors();

        FilterRegistration.Dynamic cors = env.servlets()
                .addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM,
                String.join(",", corsConfig.getAllowedOrigins()));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM,
                String.join(",", corsConfig.getAllowedMethods()));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                String.join(",", corsConfig.getAllowedHeaders()));
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");


        RabbitMqService mq = new RabbitMqService(cfg);
        env.jersey().register(new OrderResource(mq));
    }

}
