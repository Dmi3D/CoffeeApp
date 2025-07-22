package com.example.producer;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
    public void run(final ProducerConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
