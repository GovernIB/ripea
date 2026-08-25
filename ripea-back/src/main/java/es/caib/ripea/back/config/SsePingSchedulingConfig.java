package es.caib.ripea.back.config;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import es.caib.ripea.back.resourcecontroller.SseResourceController;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.service.AplicacioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Programa el ping de manteniment de les connexions SSE (SseResourceController#pingClientsSse).
 *
 * No es fa amb @Scheduled(fixedDelayString="${...}") perquè aquell placeholder el resol l'Environment
 * de Spring (application.properties, propietats de sistema...) i només s'avalua una vegada, en arrencar:
 * no llegiria mai IPA_CONFIG. Amb un Trigger propi es rellegeix la propietat abans de cada execució, de
 * manera que canviar-la a la pantalla de configuració té efecte sense reiniciar, igual que fa
 * SchedulingConfig amb les tasques periòdiques de ripea-service.
 *
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SsePingSchedulingConfig implements SchedulingConfigurer {

    /** Interval aplicat si la propietat no hi és, no és un nombre o no és positiva. */
    private static final long INTERVAL_DEFECTE_MS = 20000L;

    private final SseResourceController sseResourceController;
    private final AplicacioService aplicacioService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                () -> sseResourceController.pingClientsSse(),
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        long interval = getIntervalMillis();
                        PeriodicTrigger trigger = new PeriodicTrigger(interval, TimeUnit.MILLISECONDS);
                        trigger.setInitialDelay(interval);
                        return trigger.nextExecutionTime(triggerContext);
                    }
                });
    }

    /**
     * Llegeix l'interval de IPA_CONFIG.
     *
     * El mètode és @PermitAll, però amb jsr250Enabled l'interceptor de Spring Security exigeix igualment
     * que hi hagi un Authentication al context abans d'avaluar el permís, i el fil del planificador no en
     * té cap perquè no ve de cap petició web. Per això se simula, igual que fa SchedulingConfig amb les
     * tasques periòdiques de ripea-service.
     */
    private long getIntervalMillis() {
        try {
            createAuthenticationContext();
            String valor = aplicacioService.propertyFindByNom(PropertyConfig.SSE_PING_INTERVAL);
            if (valor != null && !valor.trim().isEmpty()) {
                long millis = Long.parseLong(valor.trim());
                if (millis > 0) {
                    return millis;
                }
                log.warn("La propietat {} ha de ser un nombre positiu de mil·lisegons, i val '{}'. S'aplica el valor per defecte ({} ms).",
                        PropertyConfig.SSE_PING_INTERVAL, valor, INTERVAL_DEFECTE_MS);
            }
        } catch (Exception e) {
            log.warn("No s'ha pogut llegir la propietat {} ({}). S'aplica el valor per defecte ({} ms).",
                    PropertyConfig.SSE_PING_INTERVAL, e.getMessage(), INTERVAL_DEFECTE_MS);
        }
        return INTERVAL_DEFECTE_MS;
    }

    /**
     * Dóna identitat al fil del planificador. Només si no en té cap, i no es neteja: aquest fil és exclusiu
     * de les tasques programades i no serveix mai cap petició d'usuari.
     */
    private void createAuthenticationContext() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = new User("SYSTEM_RIPEA", "", Collections.singletonList(new SimpleGrantedAuthority("IPA_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        }
    }

}
