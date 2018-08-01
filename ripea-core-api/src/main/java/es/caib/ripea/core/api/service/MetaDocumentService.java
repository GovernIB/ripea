/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió de meta-documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaDocumentService {

	/**
	 * Crea un nou meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaDocument
	 *            Informació del meta-document a crear.
	 * @param plantillaNom
	 *            Nom de l'arxiu de la plantilla.
	 * @param plantillaContentType
	 *            Content type de l'arxiu de la plantilla.
	 * @param plantillaContingut
	 *            Contingut de l'arxiu de la plantilla.
	 * @return El meta-document creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto create(
			Long entitatId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) throws NotFoundException;

	/**
	 * Actualitza la informació del meta-document que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaDocument
	 *            Informació del meta-document a modificar.
	 * @param plantillaNom
	 *            Nom de l'arxiu de la plantilla.
	 * @param plantillaContentType
	 *            Content type de l'arxiu de la plantilla.
	 * @param plantillaContingut
	 *            Contingut de l'arxiu de la plantilla.
	 * @return El meta-document modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto update(
			Long entitatId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) throws NotFoundException;

	/**
	 * Marca el meta-document especificada com a activa/inactiva .
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document a modificar.
	 * @param actiu
	 *            true si el meta-document es vol activar o false en cas contrari.
	 * @return El meta-document modificat
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu) throws NotFoundException;

	/**
	 * Esborra el meta-document amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document a esborrar.
	 * @return El meta-document esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto delete(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta un meta-document donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document a trobar.
	 * @return El meta-document amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta un meta-document donat el seu codi.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param codi
	 *            Atribut codi del meta-document a trobar.
	 * @return El meta-document amb el codi especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto findByEntitatCodi(
			Long entitatId,
			String codi) throws NotFoundException;

	/**
	 * Llistat amb tots els meta-documents de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return El llistat de meta-documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaDocumentDto> findByEntitat(
			Long entitatId) throws NotFoundException;

	/**
	 * Llistat paginat amb tots els meta-documents.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param paginacioParams
	 *            Peràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de meta-documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<MetaDocumentDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Llista amb tots els meta-documents actius d'una entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param incloureGlobalsExpedient
	 *            Indica si s'han de retornar els meta-documents globals per expedient.
	 * @return La llista de meta-documents actius.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaDocumentDto> findByEntitatAndActiveTrue(
			Long entitatId,
			boolean incloureGlobalsExpedient) throws NotFoundException;

	/**
	 * Retorna la plantilla asociada al meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
	 * @return La plantilla del meta-document o null si no n'hi ha.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('tothom')")
	public FitxerDto getPlantilla(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Afegeix una meta-dada al meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
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
	 * Actualitza una meta-dada del meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
	 * @param metaNodeMetaDadaId
	 *            Id de la meta-dada a actualitzar.
	 * @param multiplicitat
	 *            Multiplicitat de la meta-dada al document.
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
	 * Esborra una meta-dada del meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
	 * @param metaDocumentMetaDadaId
	 *            Id de la meta-dada a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaDelete(
			Long entitatId,
			Long id,
			Long metaDocumentMetaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-expedient una posició cap amunt.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaDocumentMetaDadaId
	 *            Id de la meta-dada a moure.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaMoveUp(
			Long entitatId,
			Long id,
			Long metaDocumentMetaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-expedient una posició cap avall.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaDocumentMetaDadaId
	 *            Id de la meta-dada a moure.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void metaDadaMoveDown(
			Long entitatId,
			Long id,
			Long metaDocumentMetaDadaId) throws NotFoundException;

	/**
	 * Mou una meta-dada del meta-document a una altra posició i reorganitza.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
	 * @param metaDocumentMetaDada
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
			Long metaDocumentMetaDada,
			int posicio) throws NotFoundException;

	/**
	 * Cerca la meta-dada del meta-document donat l'id del registre
	 * meta-node meta-dada.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param metaNodeMetaDadaId
	 *            Id de la meta-dada a trobar.
	 * @return La meta-dada del meta-document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaNodeMetaDadaDto metaDadaFind(
			Long entitatId,
			Long id,
			Long metaNodeMetaDadaId) throws NotFoundException;

	/**
	 * Cerca les meta-dades globals pels meta-documents.
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
	 * Consulta els permisos del meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<PermisDto> findPermis(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a un meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
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
	 * Esborra els permisos d'un usuari o d'un rol per a un meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-document.
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
	 * Consulta els meta-documents actius donada una entitat i un contenidor 
	 * que tenguin el permis CREATE per a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param contenidorId
	 *            Id del contenidor.
	 * @return La llista de meta-documents per crear.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaDocumentDto> findActiveByEntitatAndContenidorPerCreacio(
			Long entitatId,
			Long contenidorId) throws NotFoundException;

	/**
	 * Consulta els meta-documents actius donada una entitat i un document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param documentId
	 *            Id del document.
	 * @return La llista de meta-documents per crear.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaDocumentDto> findActiveByEntitatAndDocumentPerModificacio(
			Long entitatId,
			Long documentId) throws NotFoundException;

	/**
	 * Consulta la llista de tipus de document del plugin de portafirmes.
	 * @return La llista de tipus o null si el plugin no suporta la consulta.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<PortafirmesDocumentTipusDto> findPortafirmesDocumentTipus();

}
