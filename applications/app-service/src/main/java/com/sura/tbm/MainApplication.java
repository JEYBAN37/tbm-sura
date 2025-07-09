package com.sura.tbm;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sura.tbm", "com.sura.r2dbc","com.sura.web","com.sura.jwt"})
@OpenAPIDefinition(info = @Info(title = "APIREST TBM", version = "1.0", description = "Documentacion APIREST TBM v1.0"))
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}