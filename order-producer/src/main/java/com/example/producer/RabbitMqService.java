package com.example.producer;

import com.rabbitmq.client.*;

public class RabbitMqService {
    private final Channel channel;
    private final String exchange;

    public RabbitMqService(ProducerConfiguration cfg) throws Exception {
        ProducerConfiguration.RabbitMqConfig rmq = cfg.getRabbitmq();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rmq.host);
        factory.setPort(rmq.port);
        factory.setUsername(rmq.username);
        factory.setPassword(rmq.password);
        
        Connection conn = factory.newConnection();
        this.channel = conn.createChannel();
        this.exchange = rmq.exchange;
        
        channel.exchangeDeclare(rmq.exchange, BuiltinExchangeType.FANOUT, true);
        channel.queueDeclare(rmq.queue, true, false, false, null);
        channel.queueBind(rmq.queue, rmq.exchange, "");
    }

    public void publish(byte[] payload) throws Exception {
        channel.basicPublish(exchange, "", null, payload);
    }
}