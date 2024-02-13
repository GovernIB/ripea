package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public GrupDto delete(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta un grup donat el seu id.
	 * @param id
	 *            Atribut id del grup a trobar.
	 * 
	 * @return El grup amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public GrupDto findById(
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb tots els grups de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId TODO
	 * @param organId TODO
	 * @return La llista de grups.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<GrupDto> findByEntitatPaginat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams, 
			Long organId) throws NotFoundException;
	
	

	/**
	 * Relacionar grup amb metaexpedient
	 * 
	 * @param entitatId
	 * @param metaExpedientId
	 * @param id
	 * @param rolActual TODO
	 * @param organId TODO
	 * @param marcarPerDefecte TODO
	 */
	@PreAuthorize("hasRole('tothom')")
	public void relacionarAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, 
			Long organId, 
			boolean marcarPerDefecte);
	
	
	/**
	 * Desvincular grup amb metaexpedient
	 * 
	 * @param entitatId
	 * @param metaExpedientId
	 * @param id
	 * @param rolActual TODO
	 * @param organId TODO
	 */
	@PreAuthorize("hasRole('tothom')")
	public void desvincularAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId);

	@PreAuthorize("hasRole('tothom')")
	public List<PermisDto> findPermisos(
			Long id);

	@PreAuthorize("hasRole('tothom')")
	public void updatePermis(
			Long id,
			PermisDto permis);

	@PreAuthorize("hasRole('tothom')")
	public void deletePermis(
			Long id,
			Long permisId);

	@PreAuthorize("hasRole('tothom')")
	public boolean checkIfAlreadyExistsWithCodi(
			Long entitatId,
			String codi, 
			Long grupId);
	
	@PreAuthorize("hasRole('tothom')")
	public void marcarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId);

	@PreAuthorize("hasRole('tothom')")
	public List<GrupDto> findGrupsNoRelacionatAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long organGestorId);

	@PreAuthorize("hasRole('tothom')")
	public void esborrarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId);

	@PreAuthorize("hasRole('tothom')")
	public List<GrupDto> findGrups(
			Long entitatId,
			Long organGestorId,
			Long metaExpedientId);

	@PreAuthorize("hasRole('tothom')")
	public GrupDto findGrupById(Long grupId);

	@PreAuthorize("hasRole('tothom')")
	public GrupDto findGrupByExpedientPeticioAndProcedimentId(
			Long expedientPeticioId,
			Long procedimentId);

}