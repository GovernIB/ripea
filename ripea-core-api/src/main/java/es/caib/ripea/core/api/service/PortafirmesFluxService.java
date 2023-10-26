/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.PortafirmesCarrecDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.exception.SistemaExternException;

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
	@PreAuthorize("hasRole('tothom')")
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn,
			boolean isPlantilla) throws SistemaExternException;
	
	/**
	 * Recupera un flux de firma creat (id)
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * @return La el id del flux de firma o error.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId);
	
	/**
	 * Tanca un transacció.
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void tancarTransaccio(String idTransaccio);
	
	/**
	 * Recupera el detall d'un flux de firma creat anteriorment.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String plantillaFluxId);
	
	/**
	 * Recupera un llistat de les plantilles disponibles per un usuari aplicació
	 * @param entitatId 
	 * @param rolActual
	 * @param filtrar 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * 
	 * @return La el id del flux de firma o error.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long entitatId, String rolActual, boolean filtrar);
	
	/**
	 * Recupera un llistat de les plantilles disponibles per un usuari aplicació
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * @return La el id del flux de firma o error.
	 */
	@PreAuthorize("hasRole('tothom')")
	public boolean esborrarPlantilla(String plantillaFluxId);
	
	/**
	 * Recupera una url per mostrar la informació de una plantilla.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */
	@PreAuthorize("hasRole('tothom')")
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId);
	
	/**
	 * Recupera una url per editar una plantilla creada.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */
	@PreAuthorize("hasRole('tothom')")
	public String recuperarUrlEdicioPlantilla(
			String plantillaFluxId,
			String returnUrl);
	
	/**
	 * Recupera un llistat dels càrrecs disponibles per l'usuari aplicació configurat
	 * 
	 * @return Els càrrecs
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<PortafirmesCarrecDto> recuperarCarrecs();
	
}
