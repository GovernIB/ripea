/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.Map;

import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;

/**
 * Declaració dels mètodes per a la gestió de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDocumentFluxService {

	public Map<String, String> iniciarFluxFirma(
			String urlReturn,
			String tipusDocumentNom);
	
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId);
	
	public void tancarTransaccio(String idTransaccio);
}
