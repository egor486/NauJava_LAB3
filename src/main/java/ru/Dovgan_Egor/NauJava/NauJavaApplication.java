package ru.Dovgan_Egor.NauJava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NauJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NauJavaApplication.class, args);
	}

}
