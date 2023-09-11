package com.sabeer.electronic.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(getApiInfo());
        return docket;
    }

    private ApiInfo getApiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Electronic Store Backend : APIs",
                "This is backend project created by Sabeer",
                "1.0.0V",
                "https://www.learncodewithdurgesh.com",
                new Contact("Sabeer", "https://www.instagram.com/smart_sabeer_1995", "msabeerhussain007@gmail.com"),
                "License of APIs",
                "https://www.learncodewithdurgesh.com/aboutus",
                new ArrayList<>()
        );
        return apiInfo;
    }
}
