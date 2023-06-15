package com.example.demo.rabbit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducerClient {
    private Channel channel;
    private String EXCHANGE_NAME = "LOG_EXCHANGE";

    public RabbitMQProducerClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
    }

    public void sendInfo(String msg) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, "info", null, msg.getBytes());
    }

    public void sendError(String msg) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, "error", null, msg.getBytes());
    }
}
