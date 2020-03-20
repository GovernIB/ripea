/**
 * 
 */
package es.caib.ripea.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;

/**
 * Declaració dels mètodes per a la gestió de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortafirmesFluxService {

	/**
	 * Inicia un flux de firma
	 * 
	 * @param urlReturn
	 * 				Url on es retornarà la cridada de Portafib.
	 * @param tipusDocumentNom
	 * 				El nom del tipus de document per definir nom flux.
	 * @return El id de la transacció i la url de redirecció.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn,
			String tipusDocumentNom,
			boolean isPlantilla);
	
	/**
	 * Recupera un flux de firma creat (id)
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * @return La el id del flux de firma o error.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId);
	
	/**
	 * Tanca un transacció.
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void tancarTransaccio(String idTransaccio);
	
	/**
	 * Recupera el detall d'un flux de firma creat anteriorment.
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * @return Informació bàsica del flux de firma.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String idTransaccio);
}
