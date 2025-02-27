/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.PortafirmesCarrecDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId);
	
	/**
	 * Tanca un transacció.
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 */
	@PreAuthorize("isAuthenticated()")
	public void tancarTransaccio(String idTransaccio);
	
	/**
	 * Recupera el detall d'un flux de firma creat anteriorment.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @param signerInfo
	 * 				Indica si recuperar integrants flux
	 * @return Informació bàsica del flux de firma.
	 */
	@PreAuthorize("isAuthenticated()")
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String plantillaFluxId, boolean signerInfo);
	
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
	@PreAuthorize("isAuthenticated()")
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long entitatId, String rolActual, boolean filtrar);
	
	/**
	 * Recupera un llistat de les plantilles disponibles per un usuari aplicació
	 * 
	 * @param transaccioId
	 * 				Id de la transacció.
	 * @return La el id del flux de firma o error.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean esborrarPlantilla(String plantillaFluxId);
	
	/**
	 * Recupera una url per mostrar la informació de una plantilla.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */
	@PreAuthorize("isAuthenticated()")
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId);
	
	/**
	 * Recupera una url per editar una plantilla creada.
	 * 
	 * @param plantillaFluxId
	 * 				Id de la plantilla.
	 * @return Informació bàsica del flux de firma.
	 */
	@PreAuthorize("isAuthenticated()")
	public String recuperarUrlEdicioPlantilla(
			String plantillaFluxId,
			String returnUrl);
	
	/**
	 * Recupera un llistat dels càrrecs disponibles per l'usuari aplicació configurat
	 * 
	 * @return Els càrrecs
	 */
	@PreAuthorize("isAuthenticated()")
	public List<PortafirmesCarrecDto> recuperarCarrecs();
	
}
