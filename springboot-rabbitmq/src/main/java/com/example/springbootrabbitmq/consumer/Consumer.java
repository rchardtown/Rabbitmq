package com.example.springbootrabbitmq.consumer;

import com.example.springbootrabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author cuitao
 * @ className:
 * @ description:发布确认模式--高级篇，给 未成功发送到交换机或者队列的数据做缓存，防止数据丢失 ->消费者
 * @ create 2021-08-22 13:53
 **/
@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMessage(Message message) {
        String msg = new String(message.getBody());
        log.info("接收到消息队列：{} 的消息,消息为:{}", message.getMessageProperties().getConsumerQueue(),msg);
    }
}
