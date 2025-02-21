package es.caib.ripea.back.config;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import java.util.List;


/**
 * RequestMatcher personalitzat que comprova si una sol·licitud (request) està gestionada per un controlador que pertany a uns paquets concrets
 *
 * @author Limit Tecnologies
 */
public class MultiPackageRequestMatcher implements RequestMatcher {

    private final List<String> basePackages;

    // Constructor: assignar el paquet base que volem comprovar
    public MultiPackageRequestMatcher(String... basePackages) {
        this.basePackages = Arrays.asList(basePackages);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        // Obtenim l'objecte del controlador associat
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);

        if (handler != null) {
            String handlerClassName = handler.getClass().getName(); // Class del controlador
            // Comprova si la classe del controlador comença per qualsevol paquet de la llista
            return basePackages.stream().anyMatch(handlerClassName::startsWith);
        }

        return false; // Retorna fals si no hi ha cap controlador o no pertany als paquets
    }
}