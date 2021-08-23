package ocm.atguigu.rabbitmq.three;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import ocm.atguigu.rabbitmq.util.RabbitMqUtils;
import ocm.atguigu.rabbitmq.util.SleepUtils;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-20 16:58
 **/
public class Worker03 {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) {
        try {
            Channel channel = RabbitMqUtils.getChannel();

            //消息接收后的回调
            DeliverCallback deliverCallback = (var1, messageObject) -> {
                SleepUtils.sleep(3);
                String message = new String(messageObject.getBody());
                System.out.println("【 A 】消费者快，消费信息为： " + message);
                /**
                 * 手动应答
                 * 1.消息的标识
                 * 2.是否批量应答 true:批量，fasle:只应答当前的消息
                 */
                channel.basicAck(messageObject.getEnvelope().getDeliveryTag(), false);
            };
            //取消消费的一个回调接口
            CancelCallback cancelCallback = (var1) -> {
                System.out.println("消息被取消，消息为： " + var1);
            };
            //手动应答
            boolean ack = false;
            /**
             * 消费者消费消息
             * 1.消费哪个队列
             * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
             * 3.消费者消费的回调接口
             * 4.消费之【取消】消费的回调接口
             */

            //设置不公平分发机制
            // int prefetchCount=1;
            //设置预期值
            int prefetchCount=2;
            channel.basicQos(prefetchCount);
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
