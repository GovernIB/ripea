package es.caib.ripea.plugin.comanda;

import org.springframework.http.ResponseEntity;

import es.caib.comanda.model.management.Avis;
import es.caib.comanda.model.management.Tasca;
import es.caib.comanda.model.management.TascaPage;
import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.ripea.plugin.RipeaEndpointPluginInfo;

public interface ComandaCaibPlugin extends RipeaEndpointPluginInfo {
	public ResponseEntity<String> sendTasca(Tasca tasca) throws Exception;
	public ResponseEntity<String> deleteTasca(String idTasca) throws Exception;
	public ResponseEntity<String> sendAvis(Avis avis) throws Exception;
	public ResponseEntity<String> deleteAvis(String idAvis) throws Exception;
	public TascaPage getLlistatTasques(String quickFilter) throws Exception;
	public EstatSalutEnum getSalutComanda() throws Exception;

	/*
	 * Variants en segon pla de les crides d'escriptura, per emprar quan no cal cap dada de la resposta i no es vol
	 * que el temps de comanda s'afegeixi al de la petició de l'usuari. Retornen immediatament i no llancen cap
	 * excepció: el resultat (i l'error, si n'hi ha) arriba pel listener, que s'executa al fil de segon pla un cop
	 * acabada la crida. Els objectes que s'hi passen s'han de tenir ja construïts (no poden dependre de relacions
	 * lazy), perquè aquest fil no té ni sessió de Hibernate ni transacció.
	 */
	public void sendTascaAsync(Tasca tasca, ComandaResultatListener listener);
	public void deleteTascaAsync(String idTasca, ComandaResultatListener listener);
	public void sendAvisAsync(Avis avis, ComandaResultatListener listener);
	public void deleteAvisAsync(String idAvis, ComandaResultatListener listener);
}
