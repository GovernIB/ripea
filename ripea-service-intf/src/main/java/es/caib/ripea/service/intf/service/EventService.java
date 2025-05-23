package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;

import java.util.List;

import javax.annotation.security.PermitAll;

/**
 * Declaració dels mètodes per a la gestió d'esdeveniments SSE (Server-Sent Events).
 * Permet enviar canvis en la informació de la sessió i alertes de l'aplicació als clients connectats.
 * 
 * @author RIPEA
 */
@PermitAll
public interface EventService {
    public void notifyAvisosActius();
    public void notifyAnotacionsPendents();
    public void notifyTasquesPendents(List<String> usuarisAfectats);
    public AvisosActiusEvent getAvisosActiusEvent();
    public long getAnotacionsPendents(String usuariCodi);
    public long getTasquesPendents(String usuariCodi);
}