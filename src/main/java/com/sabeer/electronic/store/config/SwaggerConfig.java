package com.sabeer.electronic.store.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    /*
    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(getApiInfo());

        docket.securityContexts(Arrays.asList(getSecurityContext()));
        docket.securitySchemes(Arrays.asList(getSecuritySchemes()));

        ApiSelectorBuilder select = docket.select();
        select.apis(RequestHandlerSelectors.any());
        select.paths(PathSelectors.any());
        Docket build = select.build();

        return build;
    }

    private SecurityContext getSecurityContext() {
        SecurityContext context = SecurityContext.builder()
                .securityReferences(getSecurityReferences())
                .build();
        return context;
    }

    private List<SecurityReference> getSecurityReferences() {
        AuthorizationScope[] scopes = { new AuthorizationScope("Global", "Access Every Thing")};
        return Arrays.asList(new SecurityReference("JWT", scopes));
    }

    private ApiKey getSecuritySchemes() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private ApiInfo getApiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Electronic Store Backend : APIs",
                "This is backend project created by Sabeer",
                "1.0.0V",
                "https://www.sabeertech.com",
                new Contact("Sabeer", "https://www.instagram.com/smart_sabeer_1995", "msabeerhussain007@gmail.com"),
                "License of APIs",
                "https://www.sabeertech.com/aboutus",
                new ArrayList<>()
        );
        return apiInfo;
    }
     */

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Electronic Store API")
                        .description("This is electronic store project api developed by Sabeer")
                        .version("v1.0")
                        .contact(new Contact().name("Sabeer").email("msabeerhussain007@gmail.com").url("https://www.sabeertech.com"))
                        .license(new License().name("License of APIs").url("https://www.sabeertech.com/aboutus")))
                .externalDocs(new ExternalDocumentation()
                        .description("This is external url")
                        .url("https://www.sabeertech.com"));
    }
}
