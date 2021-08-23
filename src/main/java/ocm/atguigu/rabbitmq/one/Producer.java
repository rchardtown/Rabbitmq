package ocm.atguigu.rabbitmq.one;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-20 15:04
 **/
public class Producer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.106.1.83");
        factory.setUsername("admin");
        factory.setPassword("123");
        //channel 实现了自动 close 接口 自动关闭 不需要显示关闭
        try (Connection connection = factory.newConnection();
             Channel channel =
                connection.createChannel()) {
            /**
             * 生成一个队列
             * 1.队列名称
             * 2.队列里面的消息是否持久化 默认消息存储在内存中
             * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
             * 4.是否自动删除 最后一个消费者断开连接以后 该队列是否自动删除 true 自动删除
             * 5.其他参数
             */
            Map<String, Object> arguments=new HashMap<>();
            arguments.put("x-max-priority", 10);//官网规定优先级在 0-255之间，不可设置太大，耗内存和cpu
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);

            for (int i = 0; i < 10; i++) {
                String message="消息 "+i;
                if(i==5){
                    //设置优先级
                    AMQP.BasicProperties properties=new AMQP.BasicProperties().builder().priority(7).build();
                    channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
                }else{
                    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                }
            }
            /**
             * 发送一个消息
             * 1.发送到那个交换机
             * 2.路由的 key 是哪个
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */

            System.out.println("消息发送完毕");
        }
    }
}