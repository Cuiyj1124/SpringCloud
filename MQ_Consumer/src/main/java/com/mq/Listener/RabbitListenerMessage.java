package com.mq.Listener;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitListenerMessage {

    @RabbitListener(queues = "simple queue")
    public void ListenerMessage(String msg){
        System.out.println("消费者接收到消息："+msg);
    }
}
