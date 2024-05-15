package dev.zay.example.security.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dev.zay.example.security.demo.interceptor.LoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggingInterceptor())
		.addPathPatterns("/greeting/**"); // Apply intercepter to /greeting/** URLs
	}
}