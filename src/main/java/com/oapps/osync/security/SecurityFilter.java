package com.oapps.osync.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.oapps.osync.entity.AuthorizationEntity;
import com.oapps.osync.repository.AuthorizationRepo;

import lombok.extern.java.Log;

@Log
public class SecurityFilter implements Filter {

	@Autowired
	AuthorizationRepo authRepo;

	List<String> publicUrls = Arrays.asList("/api/v1/account", "/api/v1/info", "/status");

	List<String> adminUrls = Arrays.asList("/api/v1/admin/.*");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) srequest;
			HttpServletResponse response = (HttpServletResponse) sresponse;
			String header = request.getHeader("Authorization");

			String requestURI = request.getRequestURI();

			boolean isPublicUrl = isPublicUrl(requestURI);
			boolean isAdminUrl = isAdminUrl(requestURI);

			boolean isAdmin = false;
			boolean isAuthorized = false;

			if (isPublicUrl) {
				isAuthorized = true;
			} else {
				if (header != null) {
					CurrentContext.setDisableDBCheck(Boolean.TRUE);
					AuthorizationEntity authEntity = authRepo.findByToken(header);
					CurrentContext.setDisableDBCheck(Boolean.FALSE);
					if (authEntity != null) {
						isAuthorized = true;
						CurrentContext.setThreadSync(authEntity.getOsyncId(), authEntity);
						if (authEntity.isAdmin()) {
							isAdmin = true;
						}
					} 
				}

			}
//			isAuthorized = true;
			if (!isAuthorized) {
				log.info("Not authorized");
				response.getWriter().write("Not authorized");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			if (isAdminUrl && !isAdmin) {
				log.info("Not authorized.. Not an admin");
				response.getWriter().write("Not authorized");
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			log.info("Auth success");
			chain.doFilter(srequest, sresponse);
		} finally {
			CurrentContext.clear();
		}

	}

	private boolean isPublicUrl(String requestURI) {
		for (String publicUrl : publicUrls) {
			if (requestURI.equals(publicUrl)) {
				return true;
			}
			Pattern compile = Pattern.compile(publicUrl);
			if (compile.matcher(requestURI).matches()) {
				return true;
			}
		}
		return false;
	}

	private boolean isAdminUrl(String requestURI) {
		for (String adminUrl : adminUrls) {
			if (requestURI.equals(adminUrl)) {
				return true;
			}
			Pattern compile = Pattern.compile(adminUrl);
			if (compile.matcher(requestURI).matches()) {
				return true;
			}
		}
		return false;
	}
}