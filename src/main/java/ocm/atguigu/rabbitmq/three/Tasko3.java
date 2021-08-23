package ocm.atguigu.rabbitmq.three;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import ocm.atguigu.rabbitmq.util.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-20 17:27
 **/
public class Tasko3 {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        try {
            Channel channel = RabbitMqUtils.getChannel();
            /**
             * 生成一个队列
             * 1.队列名称
             * 2.队列里面的消息是否持久化 默认消息存储在内存中
             * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
             * 4.是否自动删除 最后一个消费者断开连接以后 该队列是否自动删除 true 自动删除
             * 5.其他参数
             */
            //队列持久化
            boolean durable=true;
            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
            System.out.println("输入消息：");
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNext()) {
                String message = scanner.next();
                /**
                 * 发送一个消息
                 * 1.发送到那个交换机
                 * 2.路由的 key 是哪个
                 * 3.其他的参数信息
                 * 4.发送消息的消息体
                 */
                //消息持久化:设置发送的消息为持久化的消息，即保存的磁盘中而不再是内存中
                AMQP.BasicProperties messageDuration= MessageProperties.PERSISTENT_TEXT_PLAIN;
                channel.basicPublish("", QUEUE_NAME, messageDuration, message.getBytes("UTF-8"));
            }

        } catch (Exception e) {

        }
    }
}
