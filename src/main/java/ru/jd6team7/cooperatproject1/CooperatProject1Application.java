package ru.jd6team7.cooperatproject1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@OpenAPIDefinition
@EnableScheduling
public class CooperatProject1Application {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CooperatProject1Application.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
