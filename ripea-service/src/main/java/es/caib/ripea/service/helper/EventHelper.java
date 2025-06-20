package es.caib.ripea.service.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.dto.UsuariAnotacioDto;
import es.caib.ripea.service.intf.model.sse.AnotacionsPendentsEvent;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.FirmaFinalitzadaEvent;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.TasquesPendentsEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventHelper {

	@Autowired private JmsTemplate jmsTemplate;
	@Autowired private AvisRepository avisRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private EmailHelper emailHelper;

    public void notifyAvisosActius() {
    	try {
    		AvisosActiusEvent event = getAvisosActiusEvent();
	        log.debug("notifyAvisosActius a clients");
	        jmsTemplate.convertAndSend("avisos", event);
    	} catch (Exception ex) {
    		log.error("Error al notifyAvisosActius a clients", ex);
    	}
    }

    public void notifyAnotacionsPendents(Long anotacioId) {
    	notifyAnotacionsPendents(emailHelper.dadesUsuarisAfectatsAnotacio(anotacioId));
    }
    
    public void notifyAnotacionsPendents(List<UsuariAnotacioDto> usuarisAfectats) {
    	//Aquesta funció es crida desde EmailHelper. Notificam als mateixos que rebràn el mail. TODO: ¿eliminar enviament de mail?  
    	//Grup, organ gestor i tenint en compte rols.
    	try {
    		log.debug("notifyAnotacionsPendents a clients");
    		Map<String, Long> anotacioUsuaris = new HashMap<String, Long>();
    		if (usuarisAfectats!=null) {
    			for (UsuariAnotacioDto usuari: usuarisAfectats) {
    				anotacioUsuaris.put(usuari.getCodi(), getAnotacionsPendents(usuari));
    			}
    		}
    		AnotacionsPendentsEvent resultat = new AnotacionsPendentsEvent(anotacioUsuaris);
    		jmsTemplate.convertAndSend("anotacions", resultat);
    	} catch (Exception ex) {
    		log.error("Error al notifyAnotacionsPendents a clients", ex);
    	}
    }
    
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
    		jmsTemplate.convertAndSend("tasques", resultat);
    	} catch (Exception ex) {
    		log.error("Error al notifyTasquesPendents a clients", ex);
    	}
    }
    
    public void notifyFluxFirmaFinalitzat(CreacioFluxFinalitzatEvent fluxEvent) {
    	try {
    		jmsTemplate.convertAndSend("flux", fluxEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyFluxFirmaFinalitzat a expedients suscrits", ex);
    	}
    }
    
    public void notifyFirmaNavegadorFinalitzada(FirmaFinalitzadaEvent firmaEvent) {
    	try {
    		jmsTemplate.convertAndSend("firma", firmaEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyFirmaNavegadorFinalitzada a expedients suscrits", ex);
    	}
    }
    
    public void notifyScanFinalitzat(ScanFinalitzatEvent scanEvent) {
    	try {
    		jmsTemplate.convertAndSend("scan", scanEvent);
    	} catch (Exception ex) {
    		log.error("Error al notifyScanFinalitzat a expedients suscrits", ex);
    	}
    }
    
	public long getAnotacionsPendents(UsuariAnotacioDto usuariCodi) {
		try {
			EntitatEntity entitatEntity = null;
			if (usuariCodi.getEntitatId()!=null) {
				entitatEntity = entitatRepository.findById(usuariCodi.getEntitatId()).get();
			} else {
				entitatEntity = metaExpedientRepository.findById(usuariCodi.getMetaExpedientId()).get().getEntitat();				
			}
			if (entitatEntity!=null) {
				
				OrganGestorEntity organGestorEntity = null;
				if (usuariCodi.getOrganId()!=null)
					organGestorRepository.findById(usuariCodi.getOrganId()).get();
				
				UsuariEntity usuari = usuariRepository.getOne(usuariCodi.getCodi());

				return cacheHelper.countAnotacionsPendents(
						entitatEntity,
						usuari.getRolActual(),
						usuariCodi.getCodi(),
						organGestorEntity!=null?organGestorEntity.getId():null);
			}
		} catch (Exception ex) {}
		
		return 0l;
	}

	public long getTasquesPendents(String usuariCodi) {
		try {
			return cacheHelper.countTasquesPendents(usuariCodi);
		} catch (Exception ex) {
			return 0l;
		}			
	}
    
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

}