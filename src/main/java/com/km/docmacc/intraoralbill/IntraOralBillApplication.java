package com.km.docmacc.intraoralbill;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class IntraOralBillApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntraOralBillApplication.class, args);
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("intraoral-bill-public")
				.pathsToMatch("/api/v1/intraOralBill/**")
				.build();
	}

	@Bean
	public OpenAPI maccDentalClinicOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("IntraOral Bill API")
						.description("IntraOral Bill API for MACC Dental Clinic")
						.version("v1.0.0")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}
