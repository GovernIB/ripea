package es.caib.ripea.core.helper;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.service.ConfigService;
import es.caib.ripea.core.entity.ProcesosInicialsEntity;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.ProcessosInicialsRepository;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ConfigService configService;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ExpedientRepository expedientRepository;
    @Autowired
    private ProcessosInicialsRepository processosInicialsRepository;
    @Autowired
    private ApplicationHelper applicationHelper;

    public static int counter = 0;

    private Authentication auth;

    @Synchronized
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("Executant processos inicials. Counter: " + counter++);
        addCustomAuthentication();
        try {
        	// ========================================================= EXECUTE PROCESS ONLY ONCE =================================================
            List<ProcesosInicialsEntity> processos = processosInicialsRepository.findProcesosInicialsEntityByInitTrue();
            for (ProcesosInicialsEntity proces : processos) {
                log.info("Executant procés inicial: {}",  proces.getCodi());
                switch (proces.getCodi()) {
                    case PROPIETATS_CONFIG_ENTITATS:
//                        configService.crearPropietatsConfigPerEntitats(); it must be executed on every startup because it must create configuration per entitats for properties added in the future
                        break;
                    case GENERAR_EXPEDIENT_NUMERO:
                    	generateNumeroAllExpedients();
                      break;                        
                    default:
                        log.error("Procés inicial no definit");
                        break;
                }
                applicationHelper.setProcessAsProcessed(proces.getId());
            }
            
            // ===================================================== EXECUTE PROCESS ON EVERY STARTUP OF APPLICATION ==================================
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
    
    
    private void generateNumeroAllExpedients(){
    	
		List<Long> ids = expedientRepository.findAllIdsNumeroNotNull();
		if (ids != null) {
			int i = 0;
			int size = ids.size();
			for (Long id : ids) {
				try {
					expedientHelper.generateNumeroExpedient(id);
					i++;
					if (i % 100 == 0) {
						log.info("Generant números per expedients: " + i + "/" + size + "...");
					}
				} catch (Exception e) {
					log.error("Error al generar numero expedient: " + id);
				}
				
			}
		}
    }
}
