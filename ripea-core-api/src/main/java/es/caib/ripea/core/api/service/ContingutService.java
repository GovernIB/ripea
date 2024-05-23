/**
 * 
 */
package es.caib.ripea.core.api.service;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.CodiValorDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMassiuDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Declaració dels mètodes per a gestionar continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutService {



	/**
	 * Modifica els valors de les dades d'un node.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @param valors
	 *            Valors de les dades.
	 * @param tascaId TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void dadaSave(
			Long entitatId,
			Long contingutId,
			Map<String, Object> valors, 
			Long tascaId) throws NotFoundException;

	/**
	 * Marca un contingut com a esborrat. Posteriorment un administrador
	 * el podria recuperar.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol esborrar.
	 * @param rolActual TODO
	 * @param tascaId TODO
	 * @param nomesMarcarEsborrat
	 *            Posar a true si es vol esborrar el contingut definitivament
	 *            o false si només es vol marcar com a esborrat.
	 * @throws IOException
	 *             Si s'han produit errors guardant l'arxiu al sistema
	 *             de fitxers.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void deleteReversible(
			Long entitatId,
			Long contingutId, 
			String rolActual, 
			Long tascaId) throws IOException, NotFoundException;

	/**
	 * Esborra un contingut sense possibilitat de recuperar-lo.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void deleteDefinitiu(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Recupera un contingut marcat com a esborrat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol esborrar.
	 * @throws IOException
	 *             Si s'han produit errors recuperant l'arxiu del sistema
	 *             de fitxers.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si ja existeix un altre contingut amb el mateix nom
	 *             a dins el mateix pare.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void undelete(
			Long entitatId,
			Long contingutId) throws IOException, NotFoundException, ValidationException;

	/**
	 * Mou un contingut al destí especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutOrigenId
	 *            Atribut id del contingut que es vol moure.
	 * @param contingutDestiId
	 *            Atribut id del contingut a on es vol moure l'origen.
	 * @param rolActual TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si ja existeix un altre contingut amb el mateix nom
	 *             a dins el destí.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId, String rolActual) throws NotFoundException, ValidationException;

	/**
	 * Copia un contingut al destí especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutOrigenId
	 *            Atribut id del contingut que es vol copiar.
	 * @param contingutDestiId
	 *            Atribut id del contingut a on es vol posar la còpia.
	 * @param recursiu
	 *            Amb el valor 'true' indica que .
	 * @return El contingut creat amb la còpia.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si ja existeix un altre contingut amb el mateix nom
	 *             a dins el destí.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto copy(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) throws NotFoundException, ValidationException;
	
	/**
	 * Enllaça un contingut al destí especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutOrigenId
	 *            Atribut id del contingut que es vol enllaçar.
	 * @param contingutDestiId
	 *            Atribut id del contingut a on es vol posar el contingut.
	 * @param recursiu
	 *            Amb el valor 'true' indica que .
	 * @return El contingut creat amb l'enllaç.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si ja existeix un altre contingut amb el mateix nom
	 *             a dins el destí.
	 */
	@PreAuthorize("hasRole('tothom')")
	public Long link(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) throws NotFoundException, ValidationException;

	/**
	 * Obté la informació del contingut especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @param ambFills
	 *            Indica si la resposta ha d'incloure els fills del contingut.
	 * @param ambVersions
	 *            Indica si la resposta ha d'incloure les versions del contingut.
	 * @param ambPermisos TODO
	 * @param rolActual TODO
	 * @param organActualId TODO
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, boolean ambPermisos, String rolActual, Long organActualId) throws NotFoundException;
	
	/**
	 * Obté una informació simplificada del contingut especificat per moure/copiar/vincular documents.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 *            
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto findAmbIdUserPerMoureCopiarVincular(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté la informació del contingut especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté la informació s'un contingut juntament el seu contingut donat el path.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param path
	 *            Path del contingut dins l'entitat.
	 * @return El contingut i el seu contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto getContingutAmbFillsPerPath(
			Long entitatId,
			String path) throws NotFoundException;

	/**
	 * Obté els errors de validació associades a un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @return Els errors de validació del contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ValidacioErrorDto> findErrorsValidacio(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté les alertes associades a un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @return les alertes associades a un contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<AlertaDto> findAlertes(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista d'accions realitzades damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista d'accions realitzades damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté els detalls d'una acció realitzada damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @param contingutLogId
	 *            Atribut id del log del qual es volen veure detalls.
	 * @return Els detalls de l'acció.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public ContingutLogDetallsDto findLogDetallsPerContingutAdmin(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException;

	/**
	 * Obté els detalls d'una acció realitzada damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @param contingutLogId
	 *            Atribut id del log del qual es volen veure detalls.
	 * @return Els detalls de l'acció.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista de moviments realitzats damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista de moviments realitzats damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté una llista dels continguts esborrats permetent especificar dades
	 * per al seu filtratge.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            El filtre de la consulta.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @param resultEnum
	 * @return Una pàgina amb els continguts trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public ResultDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) throws NotFoundException;
	


	/**
	 * Obté la informació del contingut emmagatzemada a l'arxiu digital.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol consultar.
	 * @return Les dades de dins l'arxiu.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ArxiuDetallDto getArxiuDetall(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
    List<CodiValorDto> sincronitzarEstatArxiu(
            Long entitatId,
            Long contingutId);

    /**
	 * Genera l'exportació en format ENI d'un document o expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol exportar.
	 * @return El fitxer amb l'exportació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto exportacioEni(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Consulta la llista d'ids de contingut segons el filtre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            Filtre per a la consulta.
	 * @param rolActual TODO
	 * @return La llista amb els ids dels continguts.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<Long> findIdsDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException;
	
	/**
	 * Consulta el amb el programar accions massives
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @param rolActual TODO
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<DocumentDto> findDocumentsMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException;
	
	/**
	 * Consulta documents definitius per executar l'acció massiva: copiar enllaç csv
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @param rolActual TODO
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<DocumentDto> findDocumentsPerCopiarCsv(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException;


	
	/**
	 * Assigna el nou ordre als fills d'un contenidor.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param orderedElements
	 *            La lista de continguts amb el nou ordre.
	 * @param contingutId
	 *            Atribut id de l'expedient pare.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si ja existeix un altre contingut amb el mateix nom
	 *             a dins el destí.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void order(
			Long entitatId,
			Long contingutId,
			Map<Integer, Long> orderedElements) throws NotFoundException, ValidationException;


	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

    Boolean netejaContingutsOrfes();
	@PreAuthorize("hasRole('IPA_SUPER')")
    ResultDocumentsSenseContingut arreglaDocumentsSenseContingut();

	public boolean isExpedient(
			Long contingutId);

	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions,
			boolean ambPermisos,
			String rolActual,
			boolean ambEntitat,
			boolean ambMapPerTipusDocument, 
			boolean ambMapPerEstat);

	@PreAuthorize("hasRole('tothom')")
	public void checkIfPermitted(
			Long contingutId,
			String rolActual, 
			PermissionEnumDto permission);

	@PreAuthorize("hasRole('tothom')")
	public Long getPareId(
			Long contingutId);

	@PreAuthorize("hasRole('tothom')")
	public Long getExpedientId(Long contingutId);
	
	@PreAuthorize("hasRole('tothom')")
	public boolean isDeleted(
			Long contingutId);

	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<ContingutMassiuDto> findDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	public List<ContingutDto> getFillsBasicInfo(Long contingutId);

	@PreAuthorize("hasRole('tothom')")
	public ContingutDto getBasicInfo(Long contingutId, boolean checkPermissions);

	@PreAuthorize("hasRole('tothom')")
	public ResultDto<ContingutMassiuDto> findDocumentsPerFirmaSimpleWebMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum);

}