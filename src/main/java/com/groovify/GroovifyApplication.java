package com.groovify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;


@SpringBootApplication
@Controller
public class GroovifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroovifyApplication.class, args);
    }
}