package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a gestionar grups.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY') or hasRole('IPA_ORGAN_ADMIN')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY') or hasRole('IPA_ORGAN_ADMIN')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY') or hasRole('IPA_ORGAN_ADMIN')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY') or hasRole('IPA_ORGAN_ADMIN')")
	public GrupDto findById(
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb tots els grups de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId TODO
	 * @param organId TODO
	 * @param filtre TODO
	 * @param resultEnum TODO
	 * @return La llista de grups.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public ResultDto<GrupDto> findByEntitat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams, 
			Long organId, 
			GrupFiltreDto filtre, 
			ResultEnumDto resultEnum) throws NotFoundException;
	
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public void desvincularAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId);

	@PreAuthorize("isAuthenticated()")
	public List<PermisDto> findPermisos(
			Long id);

	@PreAuthorize("isAuthenticated()")
	public void updatePermis(
			Long id,
			PermisDto permis);

	@PreAuthorize("isAuthenticated()")
	public void deletePermis(
			Long id,
			Long permisId);

	@PreAuthorize("isAuthenticated()")
	public boolean checkIfAlreadyExistsWithCodi(
			Long entitatId,
			String codi, 
			Long grupId);
	
	@PreAuthorize("isAuthenticated()")
	public void marcarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId);

	@PreAuthorize("isAuthenticated()")
	public List<GrupDto> findGrupsNoRelacionatAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long organGestorId);

	@PreAuthorize("isAuthenticated()")
	public void esborrarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId);

	@PreAuthorize("isAuthenticated()")
	public List<GrupDto> findGrups(
			Long entitatId,
			Long organGestorId,
			Long metaExpedientId);

	@PreAuthorize("isAuthenticated()")
	public GrupDto findGrupById(Long grupId);

	@PreAuthorize("isAuthenticated()")
	public GrupDto findGrupByExpedientPeticioAndProcedimentId(
			Long expedientPeticioId,
			Long procedimentId);

	@PreAuthorize("isAuthenticated()")
	public List<GrupDto> findGrupsPermesosProcedimentsGestioActiva(
			Long entitatId,
			String rolActual,
			Long organGestorId);

	@PreAuthorize("isAuthenticated()")
	public boolean checkIfHasGrupPerDefecte(Long procedimentId);

}