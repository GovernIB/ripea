package es.caib.ripea.core.helper;

import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.entity.ProcesosInicialsEntity;
import es.caib.ripea.core.repository.ProcessosInicialsRepository;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ProcessosInicialsRepository processosInicialsRepository;

    public static int counter = 0;

    private Authentication auth;

    @Synchronized
    @Transactional
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("Executant processos inicials. Counter: " + counter++);
        addCustomAuthentication();
        try {
        	
            List<ProcesosInicialsEntity> processos = processosInicialsRepository.findProcesosInicialsEntityByInitTrue();
            for (ProcesosInicialsEntity proces : processos) {
                log.info("Executant procés inicial: {}",  proces.getCodi());
                switch (proces.getCodi()) {
                    case PROPIETATS_CONFIG_ENTITATS:
//                        configService.crearPropietatsConfigPerEntitats(); it must be executed on every startup because it must create configuration per entititats for properties added in the future
                        break;
                    default:
                        log.error("Procés inicial no definit");
                        break;
                }
                processosInicialsRepository.updateInit(proces.getId(), false);
            }
            configService.crearPropietatsConfigPerEntitats();
            configService.actualitzarPropietatsJBossBdd();
        } catch (Exception ex) {
            log.error("Errror executant els processos inicials", ex);
        }
        restoreAuthentication();
    }

    private void addCustomAuthentication() {

        auth = SecurityContextHolder.getContext().getAuthentication();
        Principal principal = new Principal() {
            public String getName() {
                return "INIT";
            }
        };
        List<GrantedAuthority> rols = new ArrayList<>();
        rols.add(new SimpleGrantedAuthority("IPA_SUPER"));
        rols.add(new SimpleGrantedAuthority("IPA_ADMIN"));
        rols.add(new SimpleGrantedAuthority("tothom"));
        Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, "N/A", rols);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void restoreAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
