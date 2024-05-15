package dev.zay.example.security.demo.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * HandlerInterceptor allows to intercept requests and responses specifically for Spring MVC controller methods
 * Custom intercepter needs to register using a configuration class (implementing WebMvcConfigurer)
 */

@Component
public class LoggingInterceptor implements HandlerInterceptor {

	/*
	 * Called before the controller method execution. Decide whether to proceed with
	 * the request or stop processing.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("Pre Handle");
		return true;
	}

	/*
	 * Called after the controller method execution but before rendering the view.
	 * Modify the model or view attributes.
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("Post Handle");
	}

	/*
	 * Called after the complete request processing (including the view rendering)
	 * Perform cleanup or additional tasks.
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("After Completion");
	}

}
