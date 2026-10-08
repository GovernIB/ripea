package es.caib.ripea.back.interceptor;

import es.caib.ripea.service.helper.UsuarisRefreshHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrentUserHandlerInterceptor implements AsyncHandlerInterceptor {

	private final UsuarisRefreshHelper usuarisRefreshHelper;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                usuarisRefreshHelper.refreshCurrentUser();
            } catch (Exception e) {
                log.error("Error refreshing current user", e);
            }
        }
		return true;
	}

}
