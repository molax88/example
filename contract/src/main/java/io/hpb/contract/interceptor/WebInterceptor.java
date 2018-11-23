package io.hpb.contract.interceptor;

import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(modelAndView!=null) {
			if (response.getStatus() == 500) {
				modelAndView.setViewName("/error/error.html");
			} else if (response.getStatus() == 404) {
				modelAndView.setViewName("/error/error.html");
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

	/**
	 * 重定向
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean redirectToLogin(HttpServletRequest request, HttpServletResponse response, Subject currentUser)
			throws Exception {
		response.sendRedirect(request.getContextPath() + "/static/login.html");
		return false;
	}
}