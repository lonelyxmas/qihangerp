package cn.qihangerp.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"cn.qihangerp.oms"})
@ComponentScan(basePackages = {"cn.qihangerp"})
@EnableAsync
@MapperScan(basePackages = {"cn.qihangerp.module.mapper", "cn.qihangerp.module.*.mapper", "cn.qihangerp.module.open.*.mapper"})
public class OmsApi {
    public static void main(String[] args) {
        SpringApplication.run(OmsApi.class, args);
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}