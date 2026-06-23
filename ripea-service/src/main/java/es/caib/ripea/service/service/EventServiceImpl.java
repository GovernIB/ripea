package es.caib.ripea.service.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.intf.dto.UsuariAnotacioDto;
import es.caib.ripea.service.intf.dto.ValidacioErrorDto;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.ErrorsValidacioChangedEvent;
import es.caib.ripea.service.intf.model.sse.FirmaFinalitzadaEvent;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;
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

    private final EventHelper eventHelper;

    @Override
    public void notifyAvisosActius() {
    	eventHelper.notifyAvisosActius();
    }

    @Override
    public void notifyAnotacionsPendents(List<UsuariAnotacioDto> usuarisAfectats) {
    	eventHelper.notifyAnotacionsPendents(usuarisAfectats);
    }
    
    @Override
    public void notifyTasquesPendents(List<String> usuarisAfectats) {
    	eventHelper.notifyTasquesPendents(usuarisAfectats);
    }
    
    @Override
    public void notifyFluxFirmaFinalitzat(CreacioFluxFinalitzatEvent fluxEvent) {
    	eventHelper.notifyFluxFirmaFinalitzat(fluxEvent);
    }
    
    @Override
    public void notifyFirmaNavegadorFinalitzada(FirmaFinalitzadaEvent firmaEvent) {
    	eventHelper.notifyFirmaNavegadorFinalitzada(firmaEvent);
    }
    
    @Override
    public void notifyScanFinalitzat(ScanFinalitzatEvent scanEvent) {
    	eventHelper.notifyScanFinalitzat(scanEvent);
    }
    
	@Override
	public long getAnotacionsPendents(UsuariAnotacioDto usuariCodi) {
		return eventHelper.getAnotacionsPendents(usuariCodi);
	}

	@Override
	public long getTasquesPendents(String usuariCodi) {
		return eventHelper.getTasquesPendents(usuariCodi);			
	}
    
    @Override
    public AvisosActiusEvent getAvisosActiusEvent() {
    	return eventHelper.getAvisosActiusEvent();
    }

	@Override
	public AvisosActiusEvent getAvisosActiusPerUsuari(String rol, Long entitatId) {
		return eventHelper.getAvisosActiusPerUsuari(rol, entitatId);
	}

    @Scheduled(cron = "1 0 0 * * *")
    public void notifyAvisosActiusCron() {
        log.debug("Notificació dels avisos activats o desactivats per data.");
        notifyAvisosActius();
    }

	@Override
	public void notifyFluxFirmaCreat(CreacioFluxFinalitzatEvent fluxEvent) {
		eventHelper.notifyFluxFirmaCreat(fluxEvent);
	}

	@Override
	public void notifyErrorsValidacio(ErrorsValidacioChangedEvent errors) {
		eventHelper.notifyErrorsValidacio(errors);
	}

	// @Transactional perquè aquest mètode pot ser cridat des d'un fil de @JmsListener (SseResourceController), que
	// no té Open-Session-In-View; el recàlcul de validacions (findErrorsValidacioPerNode) navega per relacions lazy
	// i necessita una sessió Hibernate oberta durant tota la crida.
	@Override
	@Transactional(readOnly = true)
	public List<ValidacioErrorDto> getValidacionsInicialsExpedient(Long expedientId) {
		return eventHelper.getValidacionsInicialsExpedient(expedientId);
	}
}