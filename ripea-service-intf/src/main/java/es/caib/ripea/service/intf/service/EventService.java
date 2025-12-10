package es.caib.ripea.service.intf.service;

import java.util.List;

import javax.annotation.security.PermitAll;

import es.caib.ripea.service.intf.dto.UsuariAnotacioDto;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.FirmaFinalitzadaEvent;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;

/**
 * Declaració dels mètodes per a la gestió d'esdeveniments SSE (Server-Sent Events).
 * Permet enviar canvis en la informació de la sessió i alertes de l'aplicació als clients connectats.
 * 
 * @author RIPEA
 */
@PermitAll
public interface EventService {
    public void notifyAvisosActius();
    public void notifyAnotacionsPendents(List<UsuariAnotacioDto> usuarisAfectats);
    public void notifyTasquesPendents(List<String> usuarisAfectats);
    public void notifyFluxFirmaFinalitzat(CreacioFluxFinalitzatEvent fluxEvent);
    public void notifyFirmaNavegadorFinalitzada(FirmaFinalitzadaEvent firmaEvent);
    public void notifyScanFinalitzat(ScanFinalitzatEvent firmaEvent);
    public AvisosActiusEvent getAvisosActiusEvent();
    public long getAnotacionsPendents(UsuariAnotacioDto usuariCodi);
    public long getTasquesPendents(String usuariCodi);
}