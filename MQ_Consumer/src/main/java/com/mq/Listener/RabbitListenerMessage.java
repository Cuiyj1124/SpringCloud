package com.mq.Listener;


import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

@Component
public class RabbitListenerMessage {

    //@RabbitListener(queues = "simple queue")
    //public void ListenerMessage(String msg){
    //    System.out.println("消费者接收到消息："+msg);
    //}

    //@RabbitListener(queues = "simple queue")
    //public void ListenerWorkQueue1(String msg) throws InterruptedException {
    //    System.out.println("消费者1接收到消息："+msg+ LocalTime.now());
    //    Thread.sleep(20);
    //}
    //
    //
    //@RabbitListener(queues = "simple queue")
    //public void ListenerWorkQueue2(String msg) throws InterruptedException {
    //    System.err.println("消费者2接收到消息："+msg+ LocalTime.now());
    //    Thread.sleep(200);
    //}



    //@RabbitListener(queues = "queue1")
    //public void ListenerFanoutQueue1(String msg) throws InterruptedException {
    //    System.err.println("消费者1接收到消息："+msg+ LocalTime.now());
    //    Thread.sleep(200);
    //}
    //
    //@RabbitListener(queues = "queue2")
    //public void ListenerFanoutQueue2(String msg) throws InterruptedException {
    //    System.err.println("消费者2接收到消息："+msg+ LocalTime.now());
    //    Thread.sleep(200);
    //}


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "direct",type = ExchangeTypes.DIRECT),
            key = {"red","blue"}
    ))
    public void ListenerDirectQueue1(String msg){
        System.out.println("消费者1接收到消息："+msg);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "direct",type = ExchangeTypes.DIRECT),
            key = {"red","yellow"}
    ))
    public void ListenerDirectQueue2(String msg){
        System.out.println("消费者2接收到消息："+msg);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "topic",type = ExchangeTypes.TOPIC),
            key = "china.#"
    ))
    public void ListenerTopicQueue1(String msg){
        System.out.println("消费者1接收到消息："+msg);
    }



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue2"),
            exchange = @Exchange(name = "topic",type = ExchangeTypes.TOPIC),
            key = "#.news"
    ))
    public void ListenerTopicQueue2(String msg){
        System.out.println("消费者2接收到消息："+msg);
    }


    @RabbitListener(queues = "object.queue")
    public void ListenerObjectQueue(Map<String,Object> msg){
        System.err.println("收到消息："+msg);
    }
}
