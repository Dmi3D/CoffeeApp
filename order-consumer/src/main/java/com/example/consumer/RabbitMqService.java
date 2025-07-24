package com.example.consumer;

import com.rabbitmq.client.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.function.Consumer;

public class RabbitMqService {
    private final Channel channel;
    private final String queueName;
    private final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules(); // register JavaTimeModule for Instant

    public RabbitMqService(ConsumerConfiguration cfg) throws Exception {
        ConsumerConfiguration.RabbitMqConfig rmq = cfg.getRabbitmq();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rmq.host);
        factory.setPort(rmq.port);
        factory.setUsername(rmq.username);
        factory.setPassword(rmq.password);

        Connection connection = factory.newConnection();
        this.channel    = connection.createChannel();
        this.queueName  = rmq.queue;

        channel.exchangeDeclare(rmq.exchange, BuiltinExchangeType.FANOUT, true);
        channel.queueDeclare(rmq.queue, true, false, false, null);
        channel.queueBind(rmq.queue, rmq.exchange, "");
        channel.basicQos(1);
    }

    /** Now takes a handler that consumes an Order */
    public void listen(Consumer<Order> handler) throws IOException {
        channel.basicConsume(
                queueName,
                false,  // manual ack
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(
                            String consumerTag,
                            Envelope envelope,
                            AMQP.BasicProperties props,
                            byte[] body
                    ) throws IOException {
                        // Deserialize the JSON directly into an Order instance
                        Order order = mapper.readValue(body, Order.class);

                        // Invoke your DAO
                        handler.accept(order);

                        // Acknowledge
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
        );
    }

    public void close() throws Exception {
        channel.close();
        channel.getConnection().close();
    }
}
