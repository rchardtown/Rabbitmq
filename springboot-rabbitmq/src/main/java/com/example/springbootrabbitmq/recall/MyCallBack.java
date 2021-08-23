package com.example.springbootrabbitmq.recall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ConfirmCallback ,RabbitTemplate.ReturnCallback{
    /**
     * 交换机不管是否收到消息的一个回调方法
     * 1.发消息 交换机接收成功
     * 1.1 correlationData 保存回调消息的ID 和 相关信息
     * 1.2 交换机是否接收到消息 true :是， false ：否
     * 1.3 交换机失败接收消息的原因： 1.2 为 true 时，这里是 null
     * 2.发消息 交换机接受失败 回调
     * 2.1 correlationData 保存回调消息的ID 和 相关信息
     * 2.2  交换机是否接收到消息 true :是， false ：否
     * 2.3 交换机失败接收消息的原因
     */


    //step2 将实现的回调接口注入到 RabbitTemplate 类中，以便调用 RabbitTemplate中的 ConfirmCallback 接口时，能调用step1 中的实现类
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // step3: 具体注入
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        //设置回退消息交给谁处理
        rabbitTemplate.setReturnCallback(this);
    }

    //step1 实现回调接口
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已经收到 id 为:{}的消息", id);
        } else {
            log.info("交换机还未收到 id 为:{}消息,由于原因:{}", id, cause);
        }
    }

    /**
     * 当交换机在路由的过程中，发小消息不可到消息队列时，将消息返回给生产者
     *    即： 该接口只有在消息从叫交换机往队列发送失败时才调用
     * @param message  消息
     * @param replayCode 消息编号
     * @param replayText 回退原因
     * @param exchange 交换机
     * @param routingKey 路由key
     */
    @Override
    public void returnedMessage(Message message, int replayCode, String replayText, String exchange, String routingKey) {
        log.info("消息:{} 被服务器退回,退回原因 :{},  交换机是 :{}, 路由 key :{}",
                new String(message.getBody()),replayText, exchange, routingKey);
    }
}