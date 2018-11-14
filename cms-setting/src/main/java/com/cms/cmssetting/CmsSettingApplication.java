package com.cms.cmssetting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * @author momf
 * @date 2018-10-18
 */
@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@EnableDiscoveryClient
@MapperScan("com.cms.cmssetting")
@EnableResourceServer
@EnableAuthorizationServer
public class CmsSettingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsSettingApplication.class, args);
    }
}
