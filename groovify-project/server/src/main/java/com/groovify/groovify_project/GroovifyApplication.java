package com.groovify.groovify_project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GroovifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroovifyApplication.class, args);
	}

    @CrossOrigin
    @GetMapping("/")
    public String index() {
        return "Groovify";
    }

}
