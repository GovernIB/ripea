/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.URLInstruccioDto;
import es.caib.ripea.service.intf.dto.URLInstruccioFiltreDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió del urls d'instrucció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@PreAuthorize("isAuthenticated()")
public interface URLInstruccioService {

	/**
	 * Crea una nova url.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param url
	 *            Informació de la url a crear;
	 * @return La url creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public URLInstruccioDto create(
			Long entitatId,
			URLInstruccioDto url) throws NotFoundException;

	/**
	 * Actualitza la informació d'una url que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param url
	 *            Informació de la url a modificar.
	 * @return La url modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public URLInstruccioDto update(
			Long entitatId,
			URLInstruccioDto url) throws NotFoundException;

	/**
	 * Esborra la url amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del domini a esborrar.
	 * @return La url esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public URLInstruccioDto delete(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta una url donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del domini a cercar.
	 * @return La url amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public URLInstruccioDto findById(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb totes les urls de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de les urls.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<URLInstruccioDto> findByEntitatPaginat(
			Long entitatId, 
			URLInstruccioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	/**
	 * Llistat amb totes les urls de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de les urls.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<URLInstruccioDto> findByEntitat(
			Long entitatId) throws NotFoundException;
	

	/**
	 * Obté el valor d'una URL d'instrucció
	 * 
	 * @param entitatId
	 * @param contingutId
	 * @param urlInstruccioId
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	public String getURLInstruccio(Long entitatId, Long contingutId, Long urlInstruccioId);

}
