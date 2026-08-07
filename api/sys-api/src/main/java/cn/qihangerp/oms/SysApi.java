package cn.qihangerp.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"cn.qihangerp.oms"})
@ComponentScan(basePackages = {"cn.qihangerp"})
@MapperScan(basePackages = {"cn.qihangerp.module.mapper", "cn.qihangerp.module.*.mapper", "cn.qihangerp.module.open.*.mapper"})
public class SysApi {
    public static void main(String[] args) {
        SpringApplication.run(SysApi.class, args);
    }
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}