package com.project.delivery.config;


import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	@Bean
	public Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.project.delivery")).paths(PathSelectors.any()).build()
				.apiInfo(getApiInfo());
	}

	private ApiInfo getApiInfo() {
		return new ApiInfo("DELIVERY APP - Delivery App RESTful Web Service documentation", 
				"These pages documents the  Delivery App RESTful Web Service endpoints", 
				"v1.0",
				"Terms of Service",
				new Contact("DELIVERY", "https://www.delivery.com/", "jerichobarquez"), "", "", Collections.emptyList());
	}

}
