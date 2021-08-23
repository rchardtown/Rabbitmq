package ocm.atguigu.rabbitmq.seven;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import ocm.atguigu.rabbitmq.util.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-21 23:45
 **/
public class ReceiveLogsTopic01 {
    private static final String EXCHANGE_NAME = "topic_exchange";

    private static List<String> messageSequenceList = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //声明队列
        String queueName = "Q1";
        channel.queueDeclare(queueName, false, false, false, null);
        //通过routingKey 绑定 交换机 和 队列
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("等待接收消息...........");
        //获取队列中的消息
        DeliverCallback deliverCallback = (messageSequence, messageObject) -> {
            String message = new String(messageObject.getBody(), StandardCharsets.UTF_8.name());
            System.out.println("接受的队列名：" + queueName + " 绑定的路由健:  " + messageObject.getEnvelope().getRoutingKey()
                    + "消息：" + message
                    + " 交换机名称： " + messageObject.getEnvelope().getExchange() + "消息接收标签 :" + messageObject.getEnvelope().getDeliveryTag());
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            messageSequenceList.add(consumerTag);
            System.out.println("取消的消息序列号为: "+consumerTag);
        };
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }

}

