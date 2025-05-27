package es.caib.ripea.service.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.model.sse.AnotacionsPendentsEvent;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.FirmaFinalitzadaEvent;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.TasquesPendentsEvent;
import es.caib.ripea.service.intf.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei per a la gestió d'esdeveniments.
 * Permet enviar canvis en la informació de la sessió i alertes de l'aplicació als clients connectats.
 *
 * @author RIPEA
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final ApplicationEventPublisher eventPublisher;
    private final AvisRepository avisRepository;
    private final EntitatRepository entitatRepository;
    private final OrganGestorRepository organGestorRepository;
    private final ConversioTipusHelper conversioTipusHelper;
    private final CacheHelper cacheHelper;
    private final ConfigHelper configHelper;

    @Override
    public void notifyAvisosActius() {
    	try {
	        var event = getAvisosActiusEvent();
	        log.debug("notifyAvisosActius a clients");
	        eventPublisher.publishEvent(event);
    	} catch (Exception ex) {
    		log.error("Error al notifyAvisosActius a clients", ex);
    	}
    }

    @Override
    public void notifyAnotacionsPendents(List<String> usuarisAfectats) {
    	//Aquesta funció es crida desde EmailHelper. Notificam als mateixos que rebràn el mail. TODO: ¿eliminar enviament de mail?  
    	//Grup, organ gestor i tenint en compte rols.
    	try {
    		log.debug("notifyTasquesPendents a clients");
    		Map<String, Long> anotacioUsuaris = new HashMap<String, Long>();
    		if (usuarisAfectats!=null) {
    			for (String usuari: usuarisAfectats) {
    				anotacioUsuaris.put(usuari, getAnotacionsPendents(usuari));
    			}
    		}
    		AnotacionsPendentsEvent resultat = new AnotacionsPendentsEvent(anotacioUsuaris);
    		eventPublisher.publishEvent(resultat);
    	} catch (Exception ex) {
    		log.error("Error al notifyAnotacionsPendents a clients", ex);
    	}
    }
    
    @Override
    public void notifyTasquesPendents(List<String> usuarisAfectats) {
    	try {
    		log.debug("notifyTasquesPendents a clients");
    		Map<String, Long> tasquesUsuaris = new HashMap<String, Long>();
    		if (usuarisAfectats!=null) {
    			for (String usuari: usuarisAfectats) {
    				tasquesUsuaris.put(usuari, getTasquesPendents(usuari));
    			}
    		}
    		TasquesPendentsEvent resultat = new TasquesPendentsEvent(tasquesUsuaris);
    		eventPublisher.publishEvent(resultat);
    	} catch (Exception ex) {
    		log.error("Error al notifyTasquesPendents a clients", ex);
    	}
    }
    
    @Override
    public void notifyFluxFirmaFinalitzat(CreacioFluxFinalitzatEvent fluxEvent) {
    	try {
    		eventPublisher.publishEvent(fluxEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyFluxFirmaFinalitzat a expedients suscrits", ex);
    	}
    }
    
    @Override
    public void notifyFirmaNavegadorFinalitzada(FirmaFinalitzadaEvent firmaEvent) {
    	try {
    		eventPublisher.publishEvent(firmaEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyFirmaNavegadorFinalitzada a expedients suscrits", ex);
    	}
    }
    
    @Override
    public void notifyScanFinalitzat(ScanFinalitzatEvent scanEvent) {
    	try {
    		eventPublisher.publishEvent(scanEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyScanFinalitzat a expedients suscrits", ex);
    	}
    }
    
	@Override
	public long getAnotacionsPendents(String usuariCodi) {
		try {
			EntitatEntity entitatEntity = entitatRepository.findByCodi(configHelper.getEntitatActualCodi());
			if (entitatEntity!=null) {
				OrganGestorEntity organGestorEntity = organGestorRepository.findByCodi(usuariCodi);
				return cacheHelper.countAnotacionsPendents(
						entitatEntity,
						configHelper.getRolActual(),
						SecurityContextHolder.getContext().getAuthentication().getName(),
						organGestorEntity!=null?organGestorEntity.getId():null);
			}
		} catch (Exception ex) {}
		
		return 0l;
	}

	@Override
	public long getTasquesPendents(String usuariCodi) {
		try {
			return cacheHelper.countTasquesPendents(usuariCodi);
		} catch (Exception ex) {
			return 0l;
		}			
	}
    
    @Override
    public AvisosActiusEvent getAvisosActiusEvent() {
        var avisosUsuari = conversioTipusHelper.convertirList(
                avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE)),
                AvisDto.class);

        Map<String, List<AvisDto>> avisosAdmin = new HashMap<>();
        entitatRepository.findByActiva(true).forEach(entitat -> {
            var avisos = conversioTipusHelper.convertirList(
                    avisRepository.findActiveAdmin(DateUtils.truncate(new Date(), Calendar.DATE), entitat.getId()),
                    AvisDto.class);
            if (avisos != null && !avisos.isEmpty())
                avisosAdmin.put(entitat.getCodi(), avisos);
        });

        var event = AvisosActiusEvent.builder()
                .avisosUsuari(avisosUsuari)
                .avisosAdmin(avisosAdmin)
                .build();
        return event;
    }

    @Scheduled(cron = "1 0 0 * * *")
    public void notifyAvisosActiusCron() {
        log.debug("Notificació dels avisos activats o desactivats per data.");
        notifyAvisosActius();
    }
}