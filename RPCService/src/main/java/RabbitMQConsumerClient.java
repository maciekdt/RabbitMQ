import com.rabbitmq.client.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumerClient {

    private Channel channel;
    private String RPC_QUEUE = "RPC_QUEUE";
    private String queueName;

    public RabbitMQConsumerClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE, false, false, false, null);
    }

    public void registerConsumer() throws IOException {
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {

                String msgFileName = new String(body, "UTF-8");
                channel.basicPublish("", properties.getReplyTo(), properties,
                        getFileContent(msgFileName));
                //channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(RPC_QUEUE, true, consumer);
    }

    private byte[] getFileContent(String fileName) throws IOException {
        String filePath = fileName;
        byte[] file =  Files.readAllBytes(Paths.get(filePath));
        System.out.println(file.length);
        return file;
    }
}
