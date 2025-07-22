// src/main/java/com/example/producer/RabbitMqService.java
package com.example.producer;

import com.rabbitmq.client.*;

public class RabbitMqService {
    private final Channel channel;
    private final String exchange;

    public RabbitMqService(ProducerConfig cfg) throws Exception {
        ProducerConfig.RabbitMqConfig rmq = cfg.getRabbitmq();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rmq.host);
        factory.setPort(rmq.port);
        factory.setUsername(rmq.username);
        factory.setPassword(rmq.password);
        Connection conn = factory.newConnection();
        this.channel  = conn.createChannel();
        this.exchange = rmq.exchange;
        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);
    }

    public void publish(byte[] payload) throws Exception {
        channel.basicPublish(exchange, "", null, payload);
    }
}
