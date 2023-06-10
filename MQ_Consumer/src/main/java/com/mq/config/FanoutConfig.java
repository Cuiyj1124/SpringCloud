package com.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfig {

    //声明交换机
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanout");
    }

    //声明队列1
    @Bean
    public Queue fanoutQueue1(){
        return new Queue("queue1");
    }

    //绑定交换机和队列1
    @Bean
    public Binding bindingQueue1(){

        return BindingBuilder
                .bind(fanoutQueue1())
                .to(fanoutExchange());
    }


    @Bean
    public Queue fanoutQueue2(){
        return new Queue("queue2");
    }

    //绑定交换机和队列2
    @Bean
    public Binding bindingQueue2(){

        return BindingBuilder
                .bind(fanoutQueue2())
                .to(fanoutExchange());
    }


    @Bean
    public Queue ObjectQueue(){
        return new Queue("object.queue");
    }
}
