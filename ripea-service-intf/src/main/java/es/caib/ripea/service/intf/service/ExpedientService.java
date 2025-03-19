/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.ExpedientTancarSenseDocumentsDefinitiusException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
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
@PreAuthorize("isAuthenticated()")
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
	 * @param any
	 *            Any de l'expedient que es vol crear. Si és null l'expedient es crearà
	 *            a dins l'any actual.
	 * @param nom
	 *            Nom de l'expedient que es vol crear.
	 * @param rolActual TODO
	 * @param anexosIdsMetaDocsIdsMap
	 * @param justificantIdMetaDoc
	 * @return L'expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids o si ja existeix un
	 *             altre expedient amb el mateix tipus, sequencia i any.
	 */
	@PreAuthorize("isAuthenticated()")
	public ExpedientDto create(
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long organGestorId,
			Integer any,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId,
			String rolActual,
			Map<Long, Long> anexosIdsMetaDocsIdsMap, 
			Long justificantIdMetaDoc,
			Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap,
			PrioritatEnumDto prioritat,
			String prioritatMotiu) throws NotFoundException, ValidationException;


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
	@PreAuthorize("isAuthenticated()")
	public ExpedientDto findById(
			Long entitatId,
			Long id, 
			String rolActual) throws NotFoundException;

	/**
	 * Consulta un expedient donat el seu id.
	 * @param metaExpedientId
	 *            Atribut id del meta-expedient a partir del qual es vol crear l'expedient.
	 * @param nom
	 *            Nom de l'expedient cercat            
	 * 
	 * @return L'expedient.
	 */
	@PreAuthorize("isAuthenticated()")
	public Long checkIfExistsByMetaExpedientAndNom(
			Long metaExpedientId,
			String nom);

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
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			Long organActual) throws NotFoundException;
	
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre, 
			String rolActual,
			Long organActual) throws NotFoundException;

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
	@PreAuthorize("isAuthenticated()")
	public String agafarUser(
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
	public String agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) throws NotFoundException;

	/**
	 * Allibera un expedient agafat per l'usuari actual.
	 * EL DEIXA SENSE ASSIGNAR.
	 */
	@PreAuthorize("isAuthenticated()")
	public String alliberarUser(Long entitatId, Long id) throws NotFoundException;
	
	/**
	 * Retorna un expedient agafat per l'usuari actual al creador del expedient.
	 */
	@PreAuthorize("isAuthenticated()")
	public String retornaUser(Long entitatId, Long id) throws NotFoundException;

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
	@PreAuthorize("isAuthenticated()")
	public String tancar(
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> expedientIds,
			String format) throws IOException, NotFoundException;

	@PreAuthorize("isAuthenticated()")
	PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId, ExpedientFiltreDto filtre, Long expedientId,
			PaginacioParamsDto paginacioParams, 
			String rolActual,
			Long organActual);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	List<CodiValorDto> findByEntitat(Long entitatId);
	
	List<ExpedientDto> findByEntitatAndMetaExpedient(Long entitatId, Long metaExpedientId, String rolActual, Long organActualId);

	@PreAuthorize("isAuthenticated()")
	RespostaPublicacioComentariDto<ExpedientComentariDto> publicarComentariPerExpedient(Long entitatId, Long expedientId, String text, String rolActual);

	@PreAuthorize("isAuthenticated()")
	List<ExpedientComentariDto> findComentarisPerContingut(Long entitatId, Long expedientId);

	@PreAuthorize("isAuthenticated()")
	boolean hasWritePermission(Long expedientId);

	@PreAuthorize("isAuthenticated()")
	ExpedientDto update(
			Long entitatId,
			Long id,
			String nom,
			int any,
			Long metaExpedientDominiId,
			Long organGestorId,
			String rolActual,
			Long grupId,
			PrioritatEnumDto prioritat,
			String prioritatMotiu);

	@PreAuthorize("isAuthenticated()")
	Exception retryCreateDocFromAnnex(
			Long registreAnnexId,
			Long metaDocumentId, 
			String rolActual);


	@PreAuthorize("isAuthenticated()")
	boolean incorporar(Long entitatId,
			Long expedientId,
			Long expedientPeticioId,
			boolean associarInteressats, 
			String rolActual, 
			Map<Long, Long> anexosIdsMetaDocsIdsMap,
			Long justificantIdMetaDoc,
			boolean agafarExpedient,
		   Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap);
	
	/**
	 * Genera un índex amb el continut de l'expedient.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param format 
	 * 			  Format exportació (PDF/ZIP)
	 * @param expedientIds
	 *            Atribut id dels expedients que es volen consultar.
	 * @return Un document amb l'índex.
	 * @throws IOException 
	 */
	@PreAuthorize("isAuthenticated()")
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
	 * @param format Format pel fitxer d'exportació ("PDF" o "EXCEL").
	 * @return Un document amb l'índex.
	 * @throws IOException 
	 */
	@PreAuthorize("isAuthenticated()")
	public FitxerDto exportIndexExpedient(
			Long entitatId, 
			Set<Long> expedientIds,
			boolean exportar, String format) throws IOException;

	/**
	 * Exportació ENI de l'expedient.
	 * 
	 * @param entitatId 
	 *            Atribut id de l'entitat.
	 * @param expedientIds
	 *            Els expedients dels que vol generar l'índex
	 * @param ambDocuments
	 *            Indica si fer la expoprtació ENI dels documents (importació INSIDE)
	 * @return Document exportat ENI.
	 * @throws IOException 
	 */
	@PreAuthorize("isAuthenticated()")
	public FitxerDto exportarEniExpedient(
			Long entitatId, 
			Set<Long> expedientIds,
			boolean ambDocuments) throws IOException;

	@PreAuthorize("isAuthenticated()")
	public PaginaDto<ExpedientDto> findExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public List<Long> findIdsExpedientsPerTancamentMassiu(Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	void assignar(
			Long entitatId,
			Long expedientId,
			String usuariCodi);
	
	/**
	 * Retorna la llista dels expedients on s'ha importat el document que s'intetna importar actualment
	 * 
	 */
	@PreAuthorize("isAuthenticated()")
	public List<DocumentDto> consultaExpedientsAmbImportacio();
	
	@PreAuthorize("hasRole('IPA_ORGAN_ADMIN')")
	public boolean isOrganGestorPermes (Long expedientId, String rolActual);

	@PreAuthorize("isAuthenticated()")
	public Exception guardarExpedientArxiu(Long expId);

	@PreAuthorize("isAuthenticated()")
	public List<ExpedientDto> findByText(
			Long entitatId,
			String text, 
			String rolActual,
			Long procedimentId,
			Long organActual);


	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('IPA_DISSENY')")
	PaginaDto<ExpedientDto> findExpedientMetaExpedientPaginat(
			Long entitatId, 
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	public boolean hasReadPermissionsAny(
			String rolActual,
			Long entitatId);



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
			Long entitatId,
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
	@PreAuthorize("isAuthenticated()")
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
	 * @param expedientId
	 *            Atribut id de l'expedient fill.
	 * @param rolActual Rol actual de l'usuari que realitza l'acció
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean esborrarExpedientFill(
			Long entitatId,
			Long expedientPareId,
			Long expedientId,
			String rolActual) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public Exception retryMoverAnnexArxiu(Long registreAnnexId);

	@PreAuthorize("isAuthenticated()")
	public long countByMetaExpedient(
			Long entitatId,
			Long metaExpedientId);

	@PreAuthorize("isAuthenticated()")
	public ContingutVistaEnumDto getVistaUsuariActual();

	@PreAuthorize("isAuthenticated()")
	public void setVistaUsuariActual(
			ContingutVistaEnumDto vistaActual);


	@PreAuthorize("isAuthenticated()")
	public String getNom(
			Long id);

	@PreAuthorize("isAuthenticated()")
	ExpedientDto changeExpedientPrioritat(
			Long entitatId,
			Long expedientId,
			PrioritatEnumDto prioritat,
			String prioritatMotiu);

	@PreAuthorize("isAuthenticated()")
	void changeExpedientsPrioritat(Long entitatId, Set<Long> expedientsId, PrioritatEnumDto prioritat);
	
	@PreAuthorize("isAuthenticated()")
	public MoureDestiVistaEnumDto getVistaMoureUsuariActual();
}
