package com.myretail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyRetailConfig {

	// rest template for calling thrid party apis
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
