package es.caib.ripea.plugin.comanda;

import org.springframework.http.ResponseEntity;

import es.caib.comanda.model.management.Avis;
import es.caib.comanda.model.management.Tasca;
import es.caib.comanda.model.management.TascaPage;
import es.caib.ripea.plugin.RipeaEndpointPluginInfo;

public interface ComandaCaibPlugin extends RipeaEndpointPluginInfo {
	public ResponseEntity<String> sendTasca(Tasca tasca) throws Exception;
	public ResponseEntity<String> deleteTasca(String idTasca) throws Exception;
	public ResponseEntity<String> sendAvis(Avis avis) throws Exception;
	public ResponseEntity<String> deleteAvis(String idAvis) throws Exception;
	public TascaPage getLlistatTasques(String quickFilter) throws Exception;
}