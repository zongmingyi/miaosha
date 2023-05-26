package cn.wolfcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PersonalCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonalCenterApplication.class,args);
    }
}
