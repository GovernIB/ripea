/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Mètodes per a obtenir dades de fonts externes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesExternesService {

	/**
	 * Retorna el llistat de tots els països.
	 * 
	 * @return el llistat de països.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<PaisDto> findPaisos();

	/**
	 * Retorna el llistat de totes les províncies.
	 * 
	 * @return el llistat de províncies.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ProvinciaDto> findProvincies();

	/**
	 * Retorna el llistat de totes les comunitats.
	 * 
	 * @return el llistat de comunitats.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ComunitatDto> findComunitats();
	
	/**
	 * Retorna el llistat de totes les províncies d'una comunitat.
	 * 
	 * @return el llistat de províncies.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi);

	/**
	 * Retorna el llistat dels municipis d'una província.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return el llistat de municipis.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi);

	/**
	 * Retorna el llistat dels nivells de les administracions.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return el llistat de nivells de administracions.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<NivellAdministracioDto> findNivellAdministracions();

	@PreAuthorize("isAuthenticated()")
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(
			String provinciaCodi);

}
