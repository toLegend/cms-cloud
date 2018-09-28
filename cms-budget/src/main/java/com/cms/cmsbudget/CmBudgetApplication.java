package com.cms.cmsbudget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class CmBudgetApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmBudgetApplication.class, args);
    }
}
