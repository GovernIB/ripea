/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió dels tipus documentals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TipusDocumentalService {

	/**
	 * Crea un nou tipus documental.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param tipusDocumental
	 *            Informació del tipus documental a crear;
	 * @return El tipus documental creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public TipusDocumentalDto create(
			Long entitatId,
			TipusDocumentalDto tipusDocumental) throws NotFoundException;

	/**
	 * Actualitza la informació del meta-document que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param tipusDocumental
	 *            Informació del tipus documental a modificar.
	 * @return El tipus documental modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public TipusDocumentalDto update(
			Long entitatId,
			TipusDocumentalDto tipusDocumental) throws NotFoundException;

	/**
	 * Esborra el tipus documental amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del tipus documental a esborrar.
	 * @return El tipus documental esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public TipusDocumentalDto delete(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta un tipus documental donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del tipus documental a trobar.
	 * @return El tipus documental amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public TipusDocumentalDto findById(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb tots els tipus documentals de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de tipus documentals.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<TipusDocumentalDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	/**
	 * Llistat amb tots els tipus documentals de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de tipus documentals.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<TipusDocumentalDto> findByEntitat(
			Long entitatId) throws NotFoundException;

	/**
	 * Llista un tipus documental d'una entitat a partir del seu codi.
	 * 
	 * @param codi
	 *            Codi del tipus documental.
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return El tipus documental.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public TipusDocumentalDto findByCodiAndEntitat(
			String codi,
			Long entitatId) throws NotFoundException;
}
