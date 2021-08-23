package ocm.atguigu.rabbitmq.five;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import ocm.atguigu.rabbitmq.util.RabbitMqUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-21 14:43
 **/
public class fanoutConsumer02 {
    private final static String EXCHANGE_NAME = "logInfo";
    private final static String ROUTING_KEY = "123";

    public static void main(String[] args) throws Exception {
            Channel channel = RabbitMqUtils.getChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            /**
             * 生成一个临时的队列 队列的名称是随机的
             * 当消费者断开和该队列的连接时 队列自动删除
             */
            String queueName = channel.queueDeclare().getQueue();
            //把该临时队列绑定我们的 exchange 其中 routingkey(也称之为 binding key)为空字符串
            channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
            System.out.println("等待接收消息,把接收到的消息写到文件.....");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                File file = new File("C:\\work\\rabbitmq_info.txt");
                FileUtils.writeStringToFile(file,message,"UTF-8");
                System.out.println("数据"+message+"写入文件成功");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        }
}
