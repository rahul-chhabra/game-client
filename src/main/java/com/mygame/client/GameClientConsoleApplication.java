package com.mygame.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
* {@link GameClientConsoleApplication} is main {@link org.springframework.boot.autoconfigure.SpringBootApplication}. 
* It finds the controller and add beans.
* 
* @author Rahul
*
*/
@SpringBootApplication
public class GameClientConsoleApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameClientConsoleApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		return restTemplate;
	}

}
