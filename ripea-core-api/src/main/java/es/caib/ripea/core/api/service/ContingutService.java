/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.AnotacioRegistreFiltreDto;
import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnotacioDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;

/**
 * Declaració dels mètodes per a gestionar continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutService {

	/**
	 * Canvia el nom del contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @param nom
	 *            El nom que es vol posar al contingut.
	 * @return El contingut modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto rename(
			Long entitatId,
			Long contingutId,
			String nom) throws NotFoundException;

	/**
	 * Modifica els valors de les dades d'un node.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @param valors
	 *            Valors de les dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void dadaSave(
			Long entitatId,
			Long contingutId,
			Map<String, Object> valors) throws NotFoundException;

	/**
	 * Marca un contingut com a esborrat. Posteriorment un administrador
	 * el podria recuperar.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol esborrar.
	 * @param nomesMarcarEsborrat
	 *            Posar a true si es vol esborrar el contingut definitivament
	 *            o false si només es vol marcar com a esborrat.
	 * @return El contingut esborrat.
	 * @throws IOException
	 *             Si s'han produit errors guardant l'arxiu al sistema
	 *             de fitxers.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto deleteReversible(
			Long entitatId,
			Long contingutId) throws IOException, NotFoundException;

	/**
	 * Esborra un contingut sense possibilitat de recuperar-lo.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol esborrar.
	 * @return El contingut esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public ContingutDto deleteDefinitiu(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Recupera un contingut marcat com a esborrat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol esborrar.
	 * @return El contingut recuperat.
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
	public ContingutDto undelete(
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
	 * @return El contingut mogut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si ja existeix un altre contingut amb el mateix nom
	 *             a dins el destí.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId) throws NotFoundException, ValidationException;

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
	public ContingutDto link(
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
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions) throws NotFoundException;

	/**
	 * Obté la informació del contingut especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @param ambFills
	 *            Indica si la resposta ha d'incloure els fills del contingut.
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) throws NotFoundException;

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
	 * Obté els errors de validació d'un contingut.
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
	 * @return Una pàgina amb els continguts trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	/**
	 * Obté una llista d'anotacions de registre donades d'alta dins RIPEA
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            El filtre de la consulta.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return Una pàgina amb els continguts trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<RegistreAnotacioDto> findAnotacionsRegistre(
			Long entitatId,
			AnotacioRegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Obté una llista dels continguts esborrats permetent especificar dades
	 * per al seu filtratge.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param nom
	 *            Nom per a filtrar la llista d'elements esborrats.
	 * @param usuariCodi
	 *            Usuari per a filtrar la llista d'elements esborrats.
	 * @param dataInici
	 *            Data d'inici per a filtrar la llista d'elements esborrats.
	 * @param dataFi
	 *            Data de fi per a filtrar la llista d'elements esborrats.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return Una pàgina amb els continguts trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<ContingutDto> findEsborrats(
			Long entitatId,
			String nom,
			String usuariCodi,
			Date dataInici,
			Date dataFi,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

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
	 * @return La llista amb els ids dels continguts.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<Long> findIdsMassiusAmbFiltre(
			Long entitatId,
			ContingutMassiuFiltreDto filtre) throws NotFoundException;
	
	/**
	 * Consulta el detall de les signatures del document elegit.
	 * 
	 * @param contingut
	 *            Contingut en bytes del document.
	 * @return la informació de les signatures del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'informacio amb el contingut en bytes especificat.
	 */
	public List<ArxiuFirmaDetallDto> getDetallSignants(
			byte[] contingut) throws NotFoundException;

	/**
	 * Consulta el amb el programar accions massives
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * 
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<DocumentDto> documentMassiuFindAmbFiltre(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

}
