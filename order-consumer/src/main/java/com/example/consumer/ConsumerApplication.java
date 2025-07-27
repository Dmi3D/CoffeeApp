package com.example.consumer;

import com.google.common.io.Resources;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Environment;
import io.dropwizard.jdbi3.JdbiFactory;
import org.apache.http.HttpHost;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jdbi.v3.core.Jdbi;
import io.dropwizard.migrations.MigrationsBundle;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import io.dropwizard.setup.Bootstrap;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;

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

        // --- OpenSearch setup ---
        var osCfg = cfg.getOpensearch();
        RestHighLevelClient osClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost(osCfg.getHost(), osCfg.getPort(), osCfg.getScheme()))
        );

        // create index with our mapping if it doesn't exist
        GetIndexRequest gi = new GetIndexRequest(osCfg.getIndex());
        if (!osClient.indices().exists(gi, RequestOptions.DEFAULT)) {
            String mapping = Resources.toString(
                    Resources.getResource("opensearch/orders-index.json"),
                    StandardCharsets.UTF_8
            );
            CreateIndexRequest ci = new CreateIndexRequest(osCfg.getIndex())
                    .source(mapping, XContentType.JSON);
            osClient.indices().create(ci, RequestOptions.DEFAULT);
        }

        // service to encapsulate indexing + search
        OpenSearchService osService = new OpenSearchService(osClient, osCfg.getIndex());

        Jdbi jdbi = new JdbiFactory()
                .build(env, cfg.getDataSourceFactory(), "postgresql");

        OrderDao orderDao = jdbi.onDemand(OrderDao.class);

        RabbitMqService mq = new RabbitMqService(cfg);

        mq.listen(order -> {
            orderDao.insert(order);
            try {
                osService.indexOrder(order);
            } catch (Exception e) {
                throw new RuntimeException("Failed to index order in OpenSearch", e);
            }
        });

        env.jersey().register(new SearchResource(osService));
        env.jersey().register(new ConsumerResource(orderDao));
    }
}