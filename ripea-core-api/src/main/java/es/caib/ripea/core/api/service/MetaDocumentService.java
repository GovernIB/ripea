/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
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
	 * @param metaExpedientId
	 *            Identificador del meta-expedient pare.
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
			Long metaExpedientId,
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
	 * @param metaExpedientId
	 *            Identificador del meta-expedient pare.
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
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) throws NotFoundException;

	/**
	 * Marca el meta-document especificada com a activa/inactiva .
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Identificador del meta-expedient pare.
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
			Long metaExpedientId,
			Long id,
			boolean actiu) throws NotFoundException;

	/**
	 * Esborra el meta-document amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Identificador del meta-expedient pare.
	 * @param id
	 *            Atribut id del meta-document a esborrar.
	 * @return El meta-document esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto delete(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException;

	/**
	 * Consulta un meta-document donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Identificador del meta-expedient pare.
	 * @param id
	 *            Atribut id del meta-document a trobar.
	 * @return El meta-document amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public MetaDocumentDto findById(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException;

	/**
	 * Consulta un meta-document donat el seu codi.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Identificador del meta-expedient pare.
	 * @param codi
	 *            Atribut codi del meta-document a trobar.
	 * @return El meta-document amb el codi especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaDocumentDto findByCodi(
			Long entitatId,
			Long metaExpedientId,
			String codi) throws NotFoundException;

	/**
	 * Llistat paginat amb tots els meta-documents del meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Identificador del meta-expedient pare.
	 * @param paginacioParams
	 *            Peràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de meta-documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<MetaDocumentDto> findByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Llistat paginat amb tots els meta-documents de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de meta-documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<MetaDocumentDto> findByEntitat(
			Long entitatId) throws NotFoundException;

	/**
	 * Retorna la plantilla asociada al meta-document.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param contingutId
	 *            Identificador del contingut pare a on es vol crear el document.
	 * @param id
	 *            Atribut id del meta-document.
	 * @return La plantilla del meta-document o null si no n'hi ha.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('tothom')")
	public FitxerDto getPlantilla(
			Long entitatId,
			Long contingutId,
			Long id) throws NotFoundException;
	
	/**
	 * Retorna les dades d'un metadocument, entre elles les dades NTI.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param contingutId
	 *            Identificador del contingut pare a on es vol crear el document.
	 * @param id
	 *            Atribut id del meta-document.
	 * @return La plantilla del meta-document o null si no n'hi ha.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('tothom')")
	public MetaDocumentDto getDadesNti(
			Long entitatId,
			Long contingutId,
			Long id) throws NotFoundException;

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
	public List<MetaDocumentDto> findActiusPerCreacio(
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
	public List<MetaDocumentDto> findActiusPerModificacio(
			Long entitatId,
			Long documentId) throws NotFoundException;

	/**
	 * Consulta la llista de tipus de document del plugin de portafirmes.
	 * @return La llista de tipus o null si el plugin no suporta la consulta.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus();
	
	/**
	 * Consulta la llista de tipus de document del plugin de portafirmes.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del tipus d'expedient.
	 * @return La llista de tipus o null si el plugin no suporta la consulta.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	List<MetaDocumentDto> findByMetaExpedient(Long entitatId, Long metaExpedientId);
	
	/**
	 * Consulta la llista de tipus de document del plugin de portafirmes.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaDocumentId
	 *            Id del tipus de document.
	 * @return El tipus de document o null si el plugin no suporta la consulta.
	 */
	@PreAuthorize("hasRole('tothom')")
	MetaDocumentDto findById(
			Long entitatId,
			Long metaDocumentId);
	
	/**
	 * Consulta la llista de tipus de document del plugin de portafirmes.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param tipusGeneric
	 *            El tipus genèric del que es volen recuperar el tipus.
	 * @return El tipus de document genèric o null si el plugin no suporta la consulta.
	 */
	@PreAuthorize("hasRole('tothom')")
	MetaDocumentDto findByTipusGeneric(
			Long entitatId,
			MetaDocumentTipusGenericEnumDto tipusGeneric);

	
}
