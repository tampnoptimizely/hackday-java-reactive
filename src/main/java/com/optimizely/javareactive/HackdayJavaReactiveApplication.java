package com.optimizely.javareactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HackdayJavaReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackdayJavaReactiveApplication.class, args);
    }

}
