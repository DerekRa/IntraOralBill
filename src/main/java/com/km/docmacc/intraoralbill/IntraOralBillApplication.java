package com.km.docmacc.intraoralbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IntraOralBillApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntraOralBillApplication.class, args);
	}

}
