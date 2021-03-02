package com.db_observer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class DbObserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbObserverApplication.class, args);
    }

}
