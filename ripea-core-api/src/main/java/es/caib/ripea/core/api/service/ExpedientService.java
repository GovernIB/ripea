/**
 * 
 */
package es.caib.ripea.core.api.service;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.exception.ExpedientTancarSenseDocumentsDefinitiusException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Declaració dels mètodes per a gestionar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientService {

	/**
	 * Crea un nou expedient a dins un contenidor.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param metaExpedientId
	 *            Atribut id del meta-expedient a partir del qual es vol crear l'expedient.
	 * @param organGestorId
	 *            Atribut id de l'organ gestor responsable de l'expedient.
	 * @param pareId
	 *            Contenidor pare a on es vol crear l'expedient. Pot ser null. Si no és
	 *            null es crearà com a subexpedient d'un expedient superior.
	 * @param any
	 *            Any de l'expedient que es vol crear. Si és null l'expedient es crearà
	 *            a dins l'any actual.
	 * @param sequencia
	 *            Número de seqüència de l'expedient que es vol crear.
	 * @param nom
	 *            Nom de l'expedient que es vol crear.
	 * @param rolActual TODO
	 * @param anexosIdsMetaDocsIdsMap
	 * @return L'expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids o si ja existeix un
	 *             altre expedient amb el mateix tipus, sequencia i any.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ExpedientDto create(
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long organGestorId,
			Long pareId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId, String rolActual, 
			Map<Long, Long> anexosIdsMetaDocsIdsMap) throws NotFoundException, ValidationException;

	/**
	 * Modifica un expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient que es vol modificar.
	 * @param nom
	 *            Nom de l'expedient.
	 * @return L'expedient modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws NomInvalidException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ExpedientDto update(
			Long entitatId,
			Long id,
			String nom) throws NotFoundException, ValidationException;

	/**
	 * Consulta un expedient donat el seu id.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient que es vol trobar.
	 * @param rolActual TODO
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ExpedientDto findById(
			Long entitatId,
			Long id, 
			String rolActual) throws NotFoundException;

	/**
	 * Consulta un expedient donat el seu id.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param metaExpedientId
	 *            Atribut id del meta-expedient a partir del qual es vol crear l'expedient.
	 * @param pareId
	 *            Contenidor pare a on es vol crear l'expedient. Pot ser null. Si no és
	 *            null es crearà com a subexpedient d'un expedient superior.
	 * @param nom
	 *            Nom de l'expedient cercat            
	 * @param esborrat
	 *            Atribut id de l'expedient que es vol trobar.
	 * @param rolActual TODO
	 * @param organId TODO
	 * @return L'expedient.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ExpedientDto findByMetaExpedientAndPareAndNomAndEsborrat(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			String nom,
			int esborrat, 
			String rolActual, 
			Long organId);

	/**
	 * Consulta els expedients segons el filtre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre per a la consulta.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @param rolActual TODO
	 * @return La pàgina amb els expedients trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException;
	
	/**
	 * Consulta els expedients soble els que té permís un usuari per procediment
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param metaExpedientId
	 *            id del metaExpedient
	 * @param rolActual Rol actual de l'usuari
	 * @return Llista dels expeidents trobats
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ExpedientSelectorDto> findPerUserAndProcediment(
			Long entitatId,
			Long metaExpedientId,
			String rolActual) throws NotFoundException;

	/**
	 * Consulta la llista d'ids d'expedient segons el filtre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre per a la consulta.
	 * @param rolActual 
	 * @return La llista amb els ids dels expedients.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre, 
			String rolActual) throws NotFoundException;

	/**
	 * Posa un expedient a l'escriptori de l'usuari actual.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void agafarUser(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Posa un expedient a l'escriptori de l'usuari seleccionat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param arxiuId
	 *            Atribut id de l'arxiu al qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient.
	 * @param usuariCodi
	 *            Codi de l'usuari al qual es vol enviar l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public void agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) throws NotFoundException;

	/**
	 * Allibera un expedient agafat per l'usuari actual.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void alliberarUser(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Allibera un expedient agafat per qualsevol usuari.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void alliberarAdmin(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Tanca la tramitació d'un expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient.
	 * @param motiu
	 *            Motiu de la finalització de l'expedient.
	 * @param documentsPerFirmar
	 *            Els documents a firmar abans de tancar l'expedient.
	 * @param checkPerMassiuAdmin TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ExpedientTancarSenseDocumentsDefinitiusException
	 *             Si l'expedient no conté cap document definitiu.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void tancar(
			Long entitatId,
			Long id,
			String motiu,
			Long[] documentsPerFirmar, boolean checkPerMassiuAdmin) throws NotFoundException, ExpedientTancarSenseDocumentsDefinitiusException;

	/**
	 * Torna a l'estat obert un expedient tancat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param id
	 *            Atribut id de l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void reobrir(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Relaciona l'expedient amb un altre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param relacionatId
	 *            Atribut id de l'expedient amb que es relacionarà.
	 * @param rolActual TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void relacioCreate(
			Long entitatId,
			Long expedientId,
			Long relacionatId, String rolActual) throws NotFoundException;

	/**
	 * Esborra una relació de l'expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param relacionatId
	 *            Atribut id de l'expedient relacionat.
	 * @param rolActual TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public boolean relacioDelete(
			Long entitatId,
			Long expedientId,
			Long relacionatId, String rolActual) throws NotFoundException;

	/**
	 * Retorna la llista d'expedients relacionats amb l'expedient
	 * especificat.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'expedients relacionats.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ExpedientDto> relacioFindAmbExpedient(
			Long entitatId, 
			Long expedientId);

	/**
	 * Genera un fitxer d'exportació amb la informació dels expedients.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param expedientIds
	 *            Atribut id dels expedients a exportar.
	 * @param format
	 *            Format pel fitxer d'exportació ("ODS" o "CSV").
	 * @return El fitxer resultant de l'exportació.
	 * @throws IOException
	 *             Si ha sorgit algun problema exportant les dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> expedientIds,
			String format) throws IOException, NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId, ExpedientFiltreDto filtre, Long expedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	List<CodiValorDto> findByEntitat(Long entitatId);
	
	List<ExpedientDto> findByEntitatAndMetaExpedient(Long entitatId, Long metaExpedientId, String rolActual, Long organActualId);

	@PreAuthorize("hasRole('tothom')")
	boolean publicarComentariPerExpedient(Long entitatId, Long expedientId, String text, String rolActual);

	@PreAuthorize("hasRole('tothom')")
	List<ExpedientComentariDto> findComentarisPerContingut(Long entitatId, Long expedientId);

	@PreAuthorize("hasRole('tothom')")
	boolean hasWritePermission(Long expedientId);

	@PreAuthorize("hasRole('tothom')")
	ExpedientDto update(Long entitatId, Long id, String nom, int any, Long metaExpedientDominiId, Long organGestorId, String rolActual, Long grupId);

	@PreAuthorize("hasRole('tothom')")
	Exception retryCreateDocFromAnnex(
			Long registreAnnexId,
			Long metaDocumentId, 
			String rolActual);

	@PreAuthorize("hasRole('tothom')")
	Exception retryNotificarDistribucio(Long expedientPeticioId);

	@PreAuthorize("hasRole('tothom')")
	boolean incorporar(Long entitatId,
			Long expedientId,
			Long expedientPeticioId,
			boolean associarInteressats, 
			String rolActual, 
			Map<Long, Long> anexosIdsMetaDocsIdsMap, boolean agafarExpedient);
	
	/**
	 * Genera un índex amb el continut de l'expedient.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param format 
	 * 			  Format exportació (PDF/ZIP)
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return Un document amb l'índex.
	 * @throws IOException 
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto exportIndexExpedients(
			Long entitatId, 
			Set<Long> expedientIds,
			String format) throws IOException;
	
	/**
	 * Genera un índex amb el continut de l'expedient.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param expedientIds
	 *            Els expedients dels que vol generar l'índex
	 * @return Un document amb l'índex.
	 * @throws IOException 
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto exportIndexExpedient(
			Long entitatId, 
			Set<Long> expedientIds,
			boolean exportar) throws IOException;

	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<ExpedientDto> findExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	public List<Long> findIdsExpedientsPerTancamentMassiu(Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	void assignar(
			Long entitatId,
			Long expedientId,
			String usuariCodi);
	
	/**
	 * Retorna la llista dels expedients on s'ha importat el document que s'intetna importar actualment
	 * 
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<DocumentDto> consultaExpedientsAmbImportacio();
	
	@PreAuthorize("hasRole('IPA_ORGAN_ADMIN')")
	public boolean isOrganGestorPermes (Long expedientId, String rolActual);

	@PreAuthorize("hasRole('tothom')")
	public Exception guardarExpedientArxiu(Long expId);

	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<ExpedientDto> findByText(
			Long entitatId,
			String text);


	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	PaginaDto<ExpedientDto> findExpedientMetaExpedientPaginat(
			Long entitatId, 
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('tothom')")
	public boolean hasReadPermissionsAny(
			String rolActual,
			Long entitatId);

	@PreAuthorize("hasRole('tothom')")
	public List<ExpedientDto> findByIds(
			Long entitatId,
			Set<Long> ids);

	/**
	 * Retorna una pàgina d'expedients relacionats amb l'expedient especificat.
	 * @param filtre 
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'expedients relacionats.
	 */
	public PaginaDto<ExpedientDto> relacioFindAmbExpedientPaginat(
			Long id, 
			ExpedientFiltreDto filtre, 
			Long expedientId,
			PaginacioParamsDto paginacioDtoFromRequest);
	
	/**
	 * Afegeix un expedient relacionat a la llista de documents d'un expedient (expedient pare).
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientPareId
	 *            Atribut id de l'expedient pare.
	 * @param expedientId
	 *            Atribut id de l'expedient que s'importarà (fill)
	 * @param rolActual Rol actual de l'usuari que realitza l'acció
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void importarExpedient(
			Long entitatId,
			Long expedientPareId,
			Long expedientId,
			String rolActual) throws NotFoundException;

	/**
	 * Esborra un expedient del llistat de documents d'un expedient (expedient pare).
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param expedientPareId
	 *            Atribut id de l'expedient pare.
	 * @param relacionatId
	 *            Atribut id de l'expedient fill.
	 * @param rolActual Rol actual de l'usuari que realitza l'acció
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public boolean esborrarExpedientFill(
			Long entitatId,
			Long expedientPareId,
			Long expedientId,
			String rolActual) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	Exception retryMoverAnnexArxiu(Long registreAnnexId);

}
