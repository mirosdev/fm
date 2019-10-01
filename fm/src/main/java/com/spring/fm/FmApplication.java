package com.spring.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan({ "com.spring.fm" })
@EnableJpaRepositories({ "com.spring.fm" })
@EntityScan({ "com.spring.fm.model" })
public class FmApplication {
	public static void main(String[] args) {
		SpringApplication.run(FmApplication.class, args);
	}
}
