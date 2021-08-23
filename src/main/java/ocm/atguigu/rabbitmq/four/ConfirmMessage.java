package ocm.atguigu.rabbitmq.four;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;
import ocm.atguigu.rabbitmq.util.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-21 9:39
 **/
public class ConfirmMessage {
    public static void main(String[] args) throws Exception {
        //单个发送消息确认
        //publishMessageIndividually(); //单个确认模式 ，发送100条数据，耗时为： 7398ms
        //批量发送消息确认
        //publicMesageBatch(); // 批量确认模式 ，发送100条数据，耗时为： 1403ms
        //异步发送消息确认
        publishMessageAsync(); //异步确认模式 ，发送100条数据，耗时为： 17ms
    }

    //单个发送消息确认
    public static void publishMessageIndividually() {
        try {
            Channel channel = RabbitMqUtils.getChannel();
            //声明发布确认模式
            channel.confirmSelect();
            // 设置个队列名
            String queueName = UUID.randomUUID().toString();
            //声明一个队列
            channel.queueDeclare(queueName, true, false, false, null);
            //发布消息
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                /**
                 * 发送一个消息
                 * 1.发送到那个交换机
                 * 2.路由的 key 是哪个
                 * 3.其他的参数信息
                 * 4.发送消息的消息体
                 */
                String message = i + "";
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                //获取发布后的确认消息
                boolean falg = channel.waitForConfirms();
                if (falg) {
                    System.out.println("消息 " + message + " 确认发布，即保存磁盘成功");
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("单个确认模式 ，发送100条数据，耗时为： " + (end - start) + "ms");
            //单个确认模式 ，发送100条数据，耗时为： 7398ms
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //批量发送消息确认
    public static void publicMesageBatch() {
        Channel channel = null;
        try {
            channel = RabbitMqUtils.getChannel();
            //声明发布确认模式
            channel.confirmSelect();
            // 设置个队列名
            String queueName = UUID.randomUUID().toString();
            //声明一个队列
            channel.queueDeclare(queueName, true, false, false, null);
            //发布消息
            long start = System.currentTimeMillis();
            //去人多少条消息批量确认一次
            int batchSize = 10;
            for (int i = 0; i < 100; i++) {
                /**
                 * 发送一个消息
                 * 1.发送到那个交换机
                 * 2.路由的 key 是哪个
                 * 3.其他的参数信息
                 * 4.发送消息的消息体
                 */
                String message = i + "";
                channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                if (i % batchSize == 0) {
                    //批量发送确认消息
                    boolean falg = channel.waitForConfirms();
                    if (falg) {
                        System.out.println("消息 " + message + " 确认发布，即保存磁盘成功");
                    }
                }

            }
            long end = System.currentTimeMillis();
            System.out.println("批量确认模式 ，发送100条数据，耗时为： " + (end - start) + "ms");
            //批量确认模式 ，发送100条数据，耗时为： 1403ms
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明发布确认模式
        channel.confirmSelect();
        // 设置个队列名
        String queueName = UUID.randomUUID().toString();

        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号
         * 3.支持并发访问
         */
        ConcurrentSkipListMap<Long, Object> allConcurrentSkipListMap = new ConcurrentSkipListMap<>();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true 可以多个消费者消费
         * 4.是否自动删除 最后一个消费者断开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        channel.queueDeclare(queueName, true, false, false, null);
        long start = System.currentTimeMillis();

        //设置消息发送确认的监听

        /**
         * 确认收到消息的一个回调
         * 1.消息序列号
         * 2. 是否批量发送，true 可以确认小于等于当前序列号的消息； false 确认当前序列号消息
         */

        ConfirmCallback confirmCallback = (sequenceNumber, multiple) -> {
            if (multiple) {
                //返回的是小于等于当前序列号的未确认消息 是一个 map
                ConcurrentNavigableMap<Long, Object> confirmed =
                        allConcurrentSkipListMap.headMap(sequenceNumber, true);
                confirmed.clear();
                System.out.println("sequenceNumber :{} "+sequenceNumber);
                System.out.println("allConcurrentSkipListMap :{}"+allConcurrentSkipListMap +"  "+allConcurrentSkipListMap.size());
            } else {
                allConcurrentSkipListMap.remove(sequenceNumber);
                System.out.println("allConcurrentSkipListMap.remove:{} "+allConcurrentSkipListMap);
            }
        };
        /**
         * 确认未收到消息的一个回调
         * 1.消息序列号
         * 2. 是否批量发送，true 可以确认小于等于当前序列号的消息； false 确认当前序列号消息
         */

        ConfirmCallback noConfirmCallback = (sequenceNumber, multiple) -> {
           String messs= (String) allConcurrentSkipListMap.get(sequenceNumber);
            System.out.println("发布的消息【未得到】队列的确认,消息标识为：" + sequenceNumber+"  消息为："+messs);
        };

        channel.addConfirmListener(confirmCallback, noConfirmCallback);
        //发消息
        for (int i = 0; i < 100; i++) {

            String message = i + "";
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println("消息 " + message + " 发布成功");
            //key： 当前消息的序号，value： 当前消息
            allConcurrentSkipListMap.put(channel.getNextPublishSeqNo(), message);
        }
        long end = System.currentTimeMillis();
        System.out.println("异步确认模式 ，发送100条数据，耗时为： " + (end - start) + "ms");
        // 异步确认模式 ，发送100条数据，耗时为： 17ms
    }
}