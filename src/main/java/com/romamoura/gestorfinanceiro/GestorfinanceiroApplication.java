package com.romamoura.gestorfinanceiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class GestorfinanceiroApplication implements WebMvcConfigurer{
	public static void main(String[] args) {
		SpringApplication.run(GestorfinanceiroApplication.class, args);
	}

}
