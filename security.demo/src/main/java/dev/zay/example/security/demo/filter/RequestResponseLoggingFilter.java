package dev.zay.example.security.demo.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/*
 * Filters intercept requests before the reach the DispatcherServlet
 */

@Component
public class RequestResponseLoggingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// Log request details
		System.out.println(">>> Request: " + request.getLocalPort());

		// Continue processing the request
		chain.doFilter(request, response);

		// Log response details
		System.out.println(">>> Response Content Type: " + response.getContentType());

	}

}
