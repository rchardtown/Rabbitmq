package com.example.springbootrabbitmq.controller;

import com.example.springbootrabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/confirm")
@Slf4j
public class ProducerController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
//    @Autowired
//    private MyCallBack myCallBack;
//
//    //依赖注入 rabbitTemplate 之后再设置它的回调对象
//    @PostConstruct
//    public void init() {
//        rabbitTemplate.setConfirmCallback(myCallBack);
//    }

    @GetMapping("sendMessage/{message}")

    public void sendMessage(@PathVariable String message) {
        //指定消息 id 为 1
        CorrelationData correlationData1 = new CorrelationData("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME, ConfirmConfig.CONFIRM_ROUTINGI_KEY, message,correlationData1);
        log.info("发送消息内容1 :{}", message);
        //指定消息 id 为 2
        CorrelationData correlationData2 = new CorrelationData("2");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME, ConfirmConfig.CONFIRM_ROUTINGI_KEY+"123", message,correlationData2);
        log.info("发送消息内容2 :{}", message);
    }
}