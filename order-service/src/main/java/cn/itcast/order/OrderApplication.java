package cn.itcast.order;


import com.clients.UserClient;
import com.config.DefaultFeignConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("cn.itcast.order.mapper")
@SpringBootApplication
@EnableFeignClients(defaultConfiguration = DefaultFeignConfiguration.class,clients = {UserClient.class})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    //@Bean
    //@LoadBalanced
    //public RestTemplate restTemplate(){
    //    return new RestTemplate();
    //}

}