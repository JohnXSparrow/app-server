package com.App.security.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.StrictHttpFirewall;

public final class LoggingHttpFirewall extends StrictHttpFirewall {
	
	private static final Logger LOGGER = Logger.getLogger(LoggingHttpFirewall.class.getName());

	public LoggingHttpFirewall() {
		super();
		return;
	}
	
	@Override
	public FirewalledRequest getFirewalledRequest(final HttpServletRequest request) throws RequestRejectedException {
		try {
			return super.getFirewalledRequest(request);
		} catch (RequestRejectedException ex) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.log(Level.WARNING,
						"Intercepted RequestBlockedException: Remote Host: " + request.getRemoteHost() + " User Agent: "
								+ request.getHeader("User-Agent") + " Request URL: "
								+ request.getRequestURL().toString());
			}

			throw new RequestRejectedException(ex.getMessage() + ".\n Remote Host: " + request.getRemoteHost()
					+ "\n User Agent: " + request.getHeader("User-Agent") + "\n Request URL: "
					+ request.getRequestURL().toString()) {
				private static final long serialVersionUID = 1L;

				@Override
				public synchronized Throwable fillInStackTrace() {
					return this; // suppress the stack trace.
				}
			};
		}
	}

	@Override
	public HttpServletResponse getFirewalledResponse(final HttpServletResponse response) {
		return super.getFirewalledResponse(response);
	}
}