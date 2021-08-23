package ocm.atguigu.rabbitmq.five;

import com.rabbitmq.client.Channel;
import ocm.atguigu.rabbitmq.util.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-21 14:41
 **/
public class fanoutProducer {
    private final static String EXCHANGE_NAME = "logInfo";
    private final static String ROUTING_KEY = "123";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            /**
             * 声明一个 exchange
             * 1.exchange 的名称
             * 2.exchange 的类型
             */
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入信息");
            while (sc.hasNext()) {
                String message = sc.nextLine();
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
                System.out.println("生产者发出消息" + message);
            }
        }
    }

}
