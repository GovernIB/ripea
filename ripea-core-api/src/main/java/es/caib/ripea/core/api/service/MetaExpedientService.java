/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientMetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientService {

	/**
	 * Crea un nou meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedient
	 *            Informació del meta-expedient a crear.
	 * @return El meta-expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientDto create(
			Long entitatId,
			MetaExpedientDto metaExpedient) throws NotFoundException;

	/**
	 * Actualitza la informació del meta-expedient que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedient
	 *            Informació del meta-expedient a modificar.
	 * @return El meta-expedient modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientDto update(
			Long entitatId,
			MetaExpedientDto metaExpedient) throws NotFoundException;

	/**
	 * Marca el meta-expedient especificat com a actiu/inactiu .
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient a modificar.
	 * @param actiu
	 *            true si el meta-expedient es vol activar o false en cas contrari.
	 * @return El meta-expedient modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu) throws NotFoundException;

	/**
	 * Esborra el meta-expedient amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient a esborrar.
	 * @return El meta-expedient esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientDto delete(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta un meta-expedient donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient a trobar.
	 * @return El meta-expedient amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta un meta-expedient donat el seu codi.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param codi
	 *            Atribut codi del meta-expedient a trobar.
	 * @return El meta-expedient amb el codi especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientDto findByEntitatCodi(
			Long entitatId,
			String codi) throws NotFoundException;

	/**
	 * Consulta els meta-expedients d'una entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de meta-expedients de l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaExpedientDto> findByEntitat(
			Long entitatId) throws NotFoundException;

	/**
	 * Consulta els meta-expedients d'una entitat de forma paginada.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de meta-expedients.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<MetaExpedientDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Afegeix una meta-dada al meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaDadaId
	 *            Id de la meta-dada a afegir.
	 * @param multiplicitat
	 *            Multiplicitat de la meta-dada a l'expedient.
	 * @param readOnly
	 *            Valor de l'atribut readOnly de la meta-dada a l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaCreate(
			Long entitatId,
			Long id,
			Long metaDadaId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) throws NotFoundException;

	/**
	 * Actualitza una meta-dada del meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaNodeMetaDadaId
	 *            Id del meta-node - meta-dada a actualitzar.
	 * @param multiplicitat
	 *            Multiplicitat de la meta-dada a l'expedient.
	 * @param readOnly
	 *            Valor de l'atribut readOnly de la meta-dada a l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaUpdate(
			Long entitatId,
			Long id,
			Long metaNodeMetaDadaId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) throws NotFoundException;

	/**
	 * Esborra una meta-dada del meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaExpedientMetaDadaId
	 *            Id de la meta-dada a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaDelete(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-expedient una posició cap amunt.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaExpedientMetaDadaId
	 *            Id de la meta-dada a moure.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaMoveUp(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-expedient una posició cap avall.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaExpedientMetaDadaId
	 *            Id de la meta-dada a moure.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaMoveDown(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-expedient a una altra posició i reorganitza.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaExpedientMetaDadaId
	 *            Id de la meta-dada a moure.
	 * @param posicio
	 *            Posició a on moure la meta-dada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaMoveTo(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDadaId,
			int posicio) throws NotFoundException;

	/**
	 * Cerca la meta-dada del meta-expedient donat l'id del registre
	 * meta-node meta-dada.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaNodeMetaDadaId
	 *            Id de la meta-dada a trobar.
	 * @return La meta-dada del meta-expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaNodeMetaDadaDto metaDadaFind(
			Long entitatId,
			Long id,
			Long metaNodeMetaDadaId) throws NotFoundException;

	/**
	 * Cerca les meta-dades globals pels meta-expedients.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return Les meta-dades globals.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaDadaDto> metaDadaFindGlobals(
			Long entitatId) throws NotFoundException;

	/**
	 * Afegeix un meta-document al meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaDocumentId
	 *            Id del meta-document a afegir.
	 * @param multiplicitat
	 *            Multiplicitat del meta-document a afegir.
	 * @param readOnly
	 *            Valor de l'atribut readOnly de la meta-dada a l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDocumentCreate(
			Long entitatId,
			Long id,
			Long metaDocumentId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) throws NotFoundException;

	/**
	 * Actualitza un meta-document al meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaExpedientMetaDocumentId
	 *            Id del meta-document - meta-expedient a modificar.
	 * @param multiplicitat
	 *            Multiplicitat del meta-document a modificar.
	 * @param readOnly
	 *            Valor de l'atribut readOnly de la meta-dada a l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDocumentUpdate(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) throws NotFoundException;

	/**
	 * Esborra un meta-document del meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaDocumentId
	 *            Id del meta-document a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDocumentDelete(
			Long entitatId,
			Long id,
			Long metaDocumentId) throws NotFoundException;

	/**
	 * Cerca el meta-documents del meta-expedient donat l'id del registre
	 * meta-expedient meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaExpedientMetaDocumentId
	 *            Id del meta-document a trobar.
	 * @return La meta-dada del meta-expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientMetaDocumentDto findMetaDocument(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId) throws NotFoundException;

	/**
	 * Mou un meta-document del meta-expedient a una altra posició i reorganitza.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaExpedientMetaDocumentId
	 *            Id del meta-document a moure.
	 * @param posicio
	 *            Posició a on moure la meta-dada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDocumentMove(
			Long entitatId,
			Long id,
			Long metaExpedientMetaDocumentId,
			int posicio) throws NotFoundException;

	/**
	 * Consulta els permisos del meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<PermisDto> findPermis(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a un meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void updatePermis(
			Long entitatId,
			Long id,
			PermisDto permis) throws NotFoundException;

	/**
	 * Esborra els permisos d'un usuari o d'un rol per a un meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException;

	/**
	 * Consulta els meta-expedients actius per una entitat pels usuaris admins.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de meta-expedients actius per l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaExpedientDto> findActiusAmbEntitatPerAdmin(
			Long entitatId) throws NotFoundException;

	/**
	 * Consulta els meta-expedients actius per una entitat amb el permis CREATE per
	 * a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de meta-expedients actius per l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(
			Long entitatId) throws NotFoundException;

	/**
	 * Consulta els meta-expedients actius per una entitat amb el permis WRITE
	 * per a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de meta-expedients actius per l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(
			Long entitatId) throws NotFoundException;

	/**
	 * Consulta els meta-expedients d'una entitat amb el permis READ per
	 * a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de meta-expedients actius per l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActiusAmbEntitatPerLectura(
			Long entitatId) throws NotFoundException;

}
