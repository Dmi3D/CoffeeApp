package com.example.consumer;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ConsumerApplication extends Application<ConsumerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ConsumerApplication().run(args);
    }

    @Override
    public String getName() {
        return "Order Consumer Service";
    }

    @Override
    public void initialize(final Bootstrap<ConsumerConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final ConsumerConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
