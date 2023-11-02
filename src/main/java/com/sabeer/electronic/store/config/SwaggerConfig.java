package com.sabeer.electronic.store.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Electronic Store API",
                description = "This is backend of electronic store developed by Sabeer",
                version = "1.0V",
                contact = @Contact(
                        name = "Sabeer Hussain",
                        email = "msabeerhussain007@gmail.com",
                        url = "https://www.sabeertech.com"
                ),
                license = @License(
                        name = "OPEN License",
                        url = "https://www.sabeertech.com"
                )

        ),
        externalDocs = @ExternalDocumentation(
                description = "This is external docs",
                url = "https://www.sabeertech.com"
        )
)
@SecurityScheme(
        name = "bearerScheme",
        type= SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
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

    /*
    @Bean
    public OpenAPI openAPI() {
        String schemeName = "bearerScheme";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(schemeName)
                )
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer")
                        )
                )
                .info(new Info()
                        .title("Electronic Store API")
                        .description("This is electronic store project api developed by Sabeer")
                        .version("v1.0")
                        .contact(new Contact().name("Sabeer").email("msabeerhussain007@gmail.com").url("https://www.sabeertech.com"))
                        .license(new License().name("License of APIs").url("https://www.sabeertech.com/aboutus"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("This is external url")
                        .url("https://www.sabeertech.com")
                );
    }
     */
}
