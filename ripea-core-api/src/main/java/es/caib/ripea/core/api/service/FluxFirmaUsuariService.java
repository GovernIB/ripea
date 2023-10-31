/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.FluxFirmaUsuariDto;
import es.caib.ripea.core.api.dto.FluxFirmaUsuariFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió dels fluxos de firma d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FluxFirmaUsuariService {

	/**
	 * Crea un nou flux de firma.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param flux
	 *            Informació del flux a crear;
	 * @return El flux creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FluxFirmaUsuariDto create(
			Long entitatId,
			FluxFirmaUsuariDto flux) throws NotFoundException;

	/**
	 * Actualitza la informació d'un flux de firma que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param flux
	 *            Informació del flux a crear;
	 * @return El flux modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FluxFirmaUsuariDto update(
			Long entitatId,
			FluxFirmaUsuariDto flux) throws NotFoundException;

	/**
	 * Esborra el flux amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del flux a esborrar.
	 * @return El flux esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FluxFirmaUsuariDto delete(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta un flux de firma donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del flux de firma a cercar.
	 * @return El flux amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FluxFirmaUsuariDto findById(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb totes els fluxos de firma d'un usuari.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return El llista dels fluxos de firma.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<FluxFirmaUsuariDto> findByEntitatAndUsuariPaginat(
			Long entitatId, 
			FluxFirmaUsuariFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	/**
	 * Llistat amb tots els fluxos de firma d'un entitat i usuari.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista amb els fluxos de firma.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<FluxFirmaUsuariDto> findByEntitatAndUsuari(
			Long entitatId) throws NotFoundException;

}