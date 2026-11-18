package com.human.mini_prj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MiniPrjApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniPrjApplication.class, args);
	}

}