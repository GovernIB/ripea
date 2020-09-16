package es.caib.ripea.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a gestionar grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface GrupService {

	/**
	 * Crea un nou grup.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param grup
	 *            Informació del grup a crear;
	 * @return El grup creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public GrupDto create(
			Long entitatId,
			GrupDto grup) throws NotFoundException;

	/**
	 * Actualitza la informació del meta-document que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param grup
	 *            Informació del grup a modificar.
	 * @return El grup modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public GrupDto update(
			Long entitatId,
			GrupDto grup) throws NotFoundException;

	/**
	 * Esborra el grup amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del grup a esborrar.
	 * @return El grup esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public GrupDto delete(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta un grup donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del grup a trobar.
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public GrupDto findById(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb tots els grups de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId TODO
	 * @return La llista de grups.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<GrupDto> findByEntitatPaginat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Relacionar grup amb metaexpedient
	 * 
	 * @param entitatId
	 * @param metaExpedientId
	 * @param id
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	void relacionarAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id);
	
	
	/**
	 * Desvincular grup amb metaexpedient
	 * 
	 * @param entitatId
	 * @param metaExpedientId
	 * @param id
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	void desvincularAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id);
	
}
