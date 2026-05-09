package com.pdvapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PdvApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdvApiApplication.class, args);
    }
}
