package com.romamoura.financialmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class FinancialManagerApplication implements WebMvcConfigurer{
	public static void main(String[] args) {
		SpringApplication.run(FinancialManagerApplication.class, args);
	}

}
