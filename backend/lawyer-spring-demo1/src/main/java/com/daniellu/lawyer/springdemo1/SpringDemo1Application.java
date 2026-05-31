package com.daniellu.lawyer.springdemo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.daniellu.lawyer.springcommon",
        "com.daniellu.lawyer.springdemo1"
})
public class SpringDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringDemo1Application.class, args);
    }

}