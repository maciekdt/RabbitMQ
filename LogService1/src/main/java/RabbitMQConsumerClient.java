import com.rabbitmq.client.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumerClient {

    private Channel channel;
    private String EXCHANGE_NAME = "LOG_EXCHANGE";
    private String queueName;
    private String filePath = "info.log";

    public RabbitMQConsumerClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "info");
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
    }

    public void registerConsumer() throws IOException {
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println(msg);

                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
                writer.write(msg);
                writer.newLine();
                writer.close();
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
