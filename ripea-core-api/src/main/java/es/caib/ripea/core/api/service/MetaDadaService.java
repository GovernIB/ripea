/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDadaService {

	/**
	 * Crea una nova meta-dada.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaDada
	 *            Informació de la meta-dada a crear.
	 * @return La MetaDada creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto create(
			Long entitatId,
			MetaDadaDto metaDada) throws NotFoundException;

	/**
	 * Actualitza la informació de la meta-dada que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaDada
	 *            Informació de la meta-dada a modificar.
	 * @return La meta-dada modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto update(
			Long entitatId,
			MetaDadaDto metaDada) throws NotFoundException;

	/**
	 * Marca com a activa/inactiva la meta-dada amb el mateix id
	 * que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id de la meta-dada a esborrar.
	 * @param activa
	 *            true si la meta-dada es vol activar o false en cas
	 *            contrari.
	 * @return La meta-dada modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto updateActiva(
			Long entitatId,
			Long id,
			boolean activa) throws NotFoundException;

	/**
	 * Esborra la meta-dada amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id de la meta-dada a esborrar.
	 * @return La meta-dada esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto delete(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta una meta-dada donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id de la meta-dada a trobar.
	 * @return La meta-dada amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta una meta-dada donat el seu codi.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param codi
	 *            Atribut codi de la meta-dada a trobar.
	 * @return La meta-dada amb el codi especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto findByEntitatCodi(
			Long entitatId,
			String codi) throws NotFoundException;

	/**
	 * Llistat paginat amb totes les meta-dades.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de meta-dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<MetaDadaDto> findAllByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Llistat amb les meta-dades actives per a una entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param incloureGlobalsExpedient
	 *            Indica si s'han de retornar les meta-dades globals per expedient.
	 * @param incloureGlobalsDocument
	 *            Indica si s'han de mostrar les meta-dades globals per document.
	 * @return La pàgina de meta-dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaDadaDto> findActiveByEntitat(
			Long entitatId,
			boolean incloureGlobalsExpedient,
			boolean incloureGlobalsDocument) throws NotFoundException;

	/**
	 * Llistat amb les meta-dades d'un node.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param nodeId
	 *            Id del node.
	 * @return la llista amb les meta-dades del node.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaNodeMetaDadaDto> findByNode(
			Long entitatId,
			Long nodeId) throws NotFoundException;

	/**
	 * Llistat amb les meta-dades actives donat un meta-node.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param nodeId
	 *            Id del node.
	 * @return la llista amb les meta-dades actives.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaDadaDto> findByNodePerCreacio(
			Long entitatId,
			Long nodeId) throws NotFoundException;

}
