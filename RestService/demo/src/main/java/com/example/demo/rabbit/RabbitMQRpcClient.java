package com.example.demo.rabbit;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RabbitMQRpcClient  {

    private Channel channel;
    private String RPC_QUEUE = "RPC_QUEUE";
    private Connection connection;

    public RabbitMQRpcClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public byte[] send(String msg) throws IOException, ExecutionException, InterruptedException {

        String replyQName = channel.queueDeclare().getQueue();
        String correlationID = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(correlationID)
                .replyTo(replyQName)
                .build();

        channel.queueDeclare(RPC_QUEUE, false, false, false, null);
        channel.basicPublish("", RPC_QUEUE, props, msg.getBytes("UTF-8"));

        CompletableFuture<byte[]> response = new CompletableFuture<>();
        String tag = channel.basicConsume(replyQName, false,
                (consumerTag, returnMsg) -> {
                    if (returnMsg.getProperties().getCorrelationId().equals(correlationID))
                        response.complete(returnMsg.getBody());
                },consumerTag -> { }
        );

        byte[] result = response.get();
        channel.basicCancel(tag);
        return result;

    }
}
