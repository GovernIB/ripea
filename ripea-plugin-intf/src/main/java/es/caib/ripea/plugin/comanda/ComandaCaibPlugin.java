package es.caib.ripea.plugin.comanda;

import org.springframework.http.ResponseEntity;

import es.caib.comanda.model.management.Avis;
import es.caib.comanda.model.management.Tasca;
import es.caib.ripea.plugin.RipeaEndpointPluginInfo;

public interface ComandaCaibPlugin extends RipeaEndpointPluginInfo {
	public ResponseEntity<String> sendTasca(Tasca tasca) throws Exception;
	public ResponseEntity<String> sendAvis(Avis avis) throws Exception;
}