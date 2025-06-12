package es.caib.ripea.service.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.caib.ripea.service.helper.EventHelper;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
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
    public void notifyAnotacionsPendents(List<String> usuarisAfectats) {
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
	public long getAnotacionsPendents(String usuariCodi) {
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

    @Scheduled(cron = "1 0 0 * * *")
    public void notifyAvisosActiusCron() {
        log.debug("Notificació dels avisos activats o desactivats per data.");
        notifyAvisosActius();
    }
}