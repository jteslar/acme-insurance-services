package com.acme.services.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.acme.services"))
            .paths(PathSelectors.any())
            .build()
            .securityContexts(listOf(securityContext()))
            .securitySchemes(listOf(basicAuthScheme()));
    }

    private fun securityContext(): SecurityContext {
        return SecurityContext.builder()
            .securityReferences(listOf(defaultAuth()))
            .forPaths(PathSelectors.any())
            .build()
    }

    private fun basicAuthScheme(): SecurityScheme {
        return BasicAuth("basicAuth")
    }

    private fun defaultAuth(): SecurityReference {
        val authorizationScope = AuthorizationScope("global", "accessEverything");
        return SecurityReference("basicAuth", arrayOf(authorizationScope))
    }
}