/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.NodeDto;
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
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param metaDada
	 *            Informació de la meta-dada a crear.
	 * @return La MetaDada creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto create(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada) throws NotFoundException;

	/**
	 * Actualitza la informació de la meta-dada que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param metaDada
	 *            Informació de la meta-dada a modificar.
	 * @return La meta-dada modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto update(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada) throws NotFoundException;

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
			Long metaNodeId,
			Long id) throws NotFoundException;

	/**
	 * Marca com a activa/inactiva la meta-dada amb el mateix id
	 * que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
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
			Long metaNodeId,
			Long id,
			boolean activa) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-expedient una posició cap amunt.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param metaDadaId
	 *            Id de la meta-dada a moure.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void moveUp(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-expedient una posició cap avall.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param metaDadaId
	 *            Id de la meta-dada a moure.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void moveDown(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-document a una altra posició i reorganitza.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param metaDadaId
	 *            Id de la meta-dada a moure.
	 * @param posicio
	 *            Posició a on moure la meta-dada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void moveTo(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId,
			int posicio) throws NotFoundException;

	/**
	 * Consulta una meta-dada donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param id
	 *            Atribut id de la meta-dada a trobar.
	 * @return La meta-dada amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto findById(
			Long entitatId,
			Long metaNodeId,
			Long id) throws NotFoundException;

	/**
	 * Consulta una meta-dada donat el seu codi.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param codi
	 *            Atribut codi de la meta-dada a trobar.
	 * @return La meta-dada amb el codi especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDadaDto findByCodi(
			Long entitatId,
			Long metaNodeId,
			String codi) throws NotFoundException;

	/**
	 * Llistat paginat amb totes les meta-dades d'un meta-node.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de meta-dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<MetaDadaDto> findByMetaNodePaginat(
			Long entitatId,
			Long metaNodeId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Llistat amb les meta-dades actives per a un meta-node.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaNodeId
	 *            Id del meta-node.
	 * @return La pàgina de meta-dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaDadaDto> findActiveByMetaNode(
			Long entitatId,
			Long metaNodeId) throws NotFoundException;

	/**
	 * Llistat amb les meta-dades associades a un node.
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
	public List<MetaDadaDto> findByNode(
			Long entitatId,
			Long nodeId) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	Long findMetaNodeIdByNodeId(
			Long entitatId,
			Long nodeId);

}