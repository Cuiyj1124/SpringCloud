package com.mq;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAMQPTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testSendMessage(){

        String queueName = "simple queue";
        String message = "hello,spring amqp!";

        rabbitTemplate.convertAndSend(queueName,message);
    }

    @Test
    public void testSendMessage2WorkQueue() throws InterruptedException {
        String queueName = "simple queue";
        String message = "hello,message__";

        for (int i = 0; i <= 50; i++) {
            rabbitTemplate.convertAndSend(queueName,message+i);
            Thread.sleep(20);
        }
    }

    @Test
    public void testSendMessageFanout(){
        //交换机名称
        String exchange = "fanout";

        //消息
        String message =  "hello,everyone";

        rabbitTemplate.convertAndSend(exchange,"",message);
    }


    @Test
    public void testSendMessageDirect(){
        //交换机名称
        String exchange = "direct";

        //消息
        String message1 =  "hello,blue";

        String message2 =  "hello,yellow";

        rabbitTemplate.convertAndSend(exchange,"blue",message1);

        rabbitTemplate.convertAndSend(exchange,"yellow",message2);
    }



    @Test
    public void testSendMessageTopic(){
        //交换机名称
        String exchange = "topic";

        //消息
        String message =  "真的吊";

        rabbitTemplate.convertAndSend(exchange,"niu.news",message);
    }

    @Test
    public void testSendObjectmsg(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("你好","123");
        map.put("身高",187);
        rabbitTemplate.convertAndSend("object.queue",map);
    }
}
