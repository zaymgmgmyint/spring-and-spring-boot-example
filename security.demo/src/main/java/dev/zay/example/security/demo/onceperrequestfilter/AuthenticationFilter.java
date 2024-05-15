package dev.zay.example.security.demo.onceperrequestfilter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Servlet filters, which can be invoked before or after servlet execution, 
 * may lead to multiple invocations if a request is forwarded to another servlet with the same filter.
 * 
 * This repetition can be problematic, especially in scenarios like Spring Security, where certain actions should only occur once per request.
 * 
 * To address this, the OncePerRequestFilter can be extended, ensuring that it is executed only once for a given request, as guaranteed by Spring.
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// Check if the user is authenticated
		if (!isAuthenticated(request)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}

		// User is authenticated, continue processing the request
		filterChain.doFilter(request, response);
	}

	private boolean isAuthenticated(HttpServletRequest request) {
		// Check if the user is authenticated (e.g., session attribute, token validation, etc.)

		// Return true if authenticated, false otherwise
		return true;
	}

}
