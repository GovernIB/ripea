/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;

/**
 * Declaració dels mètodes per a la gestió de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDocumentFluxService {

	/**
	 * 
	 * @param urlReturn
	 * @param tipusDocumentNom
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public Map<String, String> iniciarFluxFirma(
			String urlReturn,
			String tipusDocumentNom);
	
	/**
	 * 
	 * @param urlReturn
	 * @param tipusDocumentNom
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId);
	
	/**
	 * 
	 * @param urlReturn
	 * @param tipusDocumentNom
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void tancarTransaccio(String idTransaccio);
}
