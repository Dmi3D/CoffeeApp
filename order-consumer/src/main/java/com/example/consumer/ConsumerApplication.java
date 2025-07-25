package com.example.consumer;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Environment;
import io.dropwizard.jdbi3.JdbiFactory;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jdbi.v3.core.Jdbi;
import io.dropwizard.migrations.MigrationsBundle;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;

public class ConsumerApplication extends Application<ConsumerConfiguration> {
    public static void main(String[] args) throws Exception {
        new ConsumerApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap<ConsumerConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<ConsumerConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ConsumerConfiguration config) {
                return config.getDataSourceFactory();
            }

            @Override
            public String getMigrationsFileName() {
                return "db/changelog-master.xml";
            }
        });
    }

    @Override
    public void run(ConsumerConfiguration cfg, Environment env) throws Exception {
        ConsumerConfiguration.CorsConfiguration corsConfig = cfg.getCors();

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

        Jdbi jdbi = new JdbiFactory()
                .build(env, cfg.getDataSourceFactory(), "postgresql");

        OrderDao orderDao = jdbi.onDemand(OrderDao.class);

        RabbitMqService mq = new RabbitMqService(cfg);
        mq.listen(orderDao::insert);

        env.jersey().register(new ConsumerResource(orderDao));
    }
}