package com.oapps.osync.security;

import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SFBean {

	@Bean
	public FilterRegistrationBean<Filter> securityFilterRegistration() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
		registration.setFilter(securityFilter());
		registration.addUrlPatterns("/*");
		registration.setName("SecurityFilter");
		registration.setOrder(1);
		return registration;
	}

	public Filter securityFilter() {
		return new SecurityFilter();
	}
}