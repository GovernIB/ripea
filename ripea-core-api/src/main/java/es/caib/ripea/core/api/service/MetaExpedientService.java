/**
 * 
 */
package es.caib.ripea.core.api.service;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

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
	 * @param rolActual TODO
	 * @param organId TODO
	 * @return El meta-expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public MetaExpedientDto create(
			Long entitatId,
			MetaExpedientDto metaExpedient, String rolActual, Long organId) throws NotFoundException;

	/**
	 * Actualitza la informació del meta-expedient que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedient
	 *            Informació del meta-expedient a modificar.
	 * @param rolActual TODO
	 * @param estatAnterior
	 * 			  Indica si la modificació del meta-expedient és un canvi d'estat 
	 * 			  de disseny a pendent per part de l'admin d'òrgan.
	 * @param organId TODO
	 * @return El meta-expedient modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public MetaExpedientDto update(
			Long entitatId,
			MetaExpedientDto metaExpedient,
			String rolActual,
			MetaExpedientRevisioEstatEnumDto estatAnterior,
			Long organId) throws NotFoundException;
	
	/**
	 * Marca el meta-expedient especificat com a actiu/inactiu .
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient a modificar.
	 * @param actiu
	 *            true si el meta-expedient es vol activar o false en cas contrari.
	 * @param rolActual TODO
	 * @param organId TODO
	 * @return El meta-expedient modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public MetaExpedientDto updateActiu(
			Long entitatId,
			Long id,
			boolean actiu, String rolActual, Long organId) throws NotFoundException;

	/**
	 * Esborra el meta-expedient amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient a esborrar.
	 * @param organId TODO
	 * @return El meta-expedient esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SQLException 
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public MetaExpedientDto delete(
			Long entitatId,
			Long id, Long organId) throws NotFoundException;

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
	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * TODO: mirar de fusionar amb findById
	 * 
	 * Consulta un metaexpedient comprovant si l'usuari autenticat hi té permisos
	 * d'administració
	 * 
	 * @param entitatId Id de l'entitat.
	 * @param id        Atribut id del meta-expedient.
	 * @param organId TODO
	 * @return El metaexpedient amb l'identificador indicat per paràmetre
	 * 
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientDto getAndCheckAdminPermission(Long entitatId, Long id, Long organId);
	
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
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
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findByEntitat(
			Long entitatId) throws NotFoundException;

	/**
	 * Consulta els meta-expedients actius per una entitat amb el permis CREATE per
	 * a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param rolActual TODO
	 * @return La llista de meta-expedients actius per l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(
			Long entitatId, String rolActual) throws NotFoundException;

	/**
	 * Consulta els meta-expedients actius per una entitat amb el permis WRITE
	 * per a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param rolActual TODO
	 * @return La llista de meta-expedients actius per l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(
			Long entitatId, String rolActual) throws NotFoundException;

	/**
	 * Consulta els meta-expedients d'una entitat amb el permis READ per
	 * a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtreNomOrCodiSia TODO
	 * @param rolActual TODO
	 * @param comu TODO
	 * @param organId TODO
	 * @return La llista de meta-expedients actius per l'entitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActius(
			Long entitatId, String filtreNomOrCodiSia, String rolActual, boolean comu, Long organId) throws NotFoundException;

	/**
	 * Retorna el pròxim número de seqüència per a un meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param any
	 *            L'any per a obtenir el número de seqüència.
	 * @return El pròxim número de seqüència.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public long getProximNumeroSequencia(
			Long entitatId,
			Long id,
			int any) throws NotFoundException;

	/**
	 * Crea una tasca del meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param metaExpedientTasca
	 *            Informació de la tasca del meta-expedient a crear.
	 * @param rolActual TODO
	 * @param organId TODO
	 * @return El meta-expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca, String rolActual, Long organId) throws NotFoundException;

	/**
	 * Actualitza la informació de la tasca del meta-expedient que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param metaExpedientTasca
	 *            Informació de la tasca del meta-expedient a modificar.
	 * @param rolActual TODO
	 * @param organId TODO
	 * @return La tasca del meta-expedient modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientTascaDto tascaUpdate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca, String rolActual, Long organId) throws NotFoundException;

	/**
	 * Marca la tasca del meta-expedient com a activa/inactiva .
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param id
	 *            Id de la tasca del meta-expedient a modificar.
	 * @param activa
	 *            true si el meta-expedient es vol activar o false en cas contrari.
	 * @param rolActual TODO
	 * @param organId TODO
	 * @return El meta-expedient modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientTascaDto tascaUpdateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean activa, String rolActual, Long organId) throws NotFoundException;

	/**
	 * Esborra la tasca del meta-expedient amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param id
	 *            Id de la tasca del meta-expedient a esborrar.
	 * @param rolActual TODO
	 * @param organId TODO
	 * @return La tasca del meta-expedient esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientTascaDto tascaDelete(
			Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId) throws NotFoundException;

	/**
	 * Consulta una tasca del meta-expedient donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param id
	 *            Id de la tasca del meta-expedient a trobar.
	 * @return La tasca del meta-expedient amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientTascaDto tascaFindById(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException;

	/**
	 * Consulta les tasques d'un meta-expedient de forma paginada.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de tasques del meta-expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<MetaExpedientTascaDto> tascaFindPaginatByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

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
	@PreAuthorize("hasRole('tothom')")
	public List<PermisDto> permisFind(
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
	 * @param rolActual TODO
	 * @param organId TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis, String rolActual, Long organId) throws NotFoundException;

	/**
	 * Esborra els permisos d'un usuari o d'un rol per a un meta-expedient.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del meta-expedient.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @param organGestorId
	 *            Si no és null indica que el permís a esborrar està associat a un òrgan gestor.
	 * @param rolActual TODO
	 * @param organId TODO
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId,
			Long organGestorId, String rolActual, Long organId) throws NotFoundException;

	 /**
	  * Consulta els meta-expedients d'una entitat que tenen algún organ gestor asociat de forma paginada.
	  * 
	  * @param entitatId
	  *            Id de l'entitat.
	 * @param paginacioParams
	  *            Paràmetres per a dur a terme la paginació del resultats.
	 * @param rolActual TODO
	  * @return La pàgina de meta-expedients.
	  * @throws NotFoundException
	  *             Si no s'ha trobat l'objecte amb l'id especificat.
	  */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<MetaExpedientDto> findByEntitatOrOrganGestor(
			Long entitatId,
			Long organGestorId,
			MetaExpedientFiltreDto filtre,
			boolean isRolActualAdministradorOrgan,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			boolean hasPermisAdmComu);

	@PreAuthorize("hasRole('tothom')")
	public List<GrupDto> findGrupsAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId);

	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActiusAmbOrganGestorPermisLectura(
			Long entitatId,
			Long organGestorId, 
			String filtre);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public List<ArbreDto<MetaExpedientCarpetaDto>> findArbreCarpetesMetaExpedient(Long entitatId, Long metaExpedientId);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public MetaExpedientCarpetaDto deleteCarpetaMetaExpedient(Long entitatId, Long metaExpedientCarpetaId);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public ProcedimentDto findProcedimentByCodiSia(
			Long entitatId,
			String codiDir3, String codiSia);
	
	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findByCodiSia(
			Long entitatId,
			String codiSia);

	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientDto canviarEstatRevisioASellecionat(
			Long entitatId,
			MetaExpedientDto metaExpedient, 
			String rolActual);


	@PreAuthorize("hasRole('tothom')")
	public int countMetaExpedientsPendentRevisar(Long entitatId);

	@PreAuthorize("hasRole('tothom')")
	boolean isMetaExpedientPendentRevisio(
			Long entitatId,
			Long id);
	
	@PreAuthorize("hasRole('tothom')")
	boolean comprovarPermisosMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PermissionEnumDto permission);

	@PreAuthorize("hasRole('tothom')")
	List<MetaExpedientDto> findCreateWritePerm(
			Long entitatId,
			String rolActual);

	@PreAuthorize("hasRole('tothom')")
	boolean isRevisioActiva();

	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findActiusAmbEntitatPerConsultaEstadistiques(
			Long entitatId, 
			String filtreNomOrCodiSia,
			String rolActual);
	
	/**
	 * Marcar com a pendent de revisió el meta-expedient que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param organId TODO
	 * @param id
	 *            Informació del meta-expedient a marcar com a pendent.
	 * @return El meta-expedient modificat.
	 * @throws NotFoundException TODO
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public MetaExpedientDto marcarPendentRevisio(
			Long entitatId, 
			Long id, Long organId);

	/**
	 * Marcar com en procés de disseny el meta-expedient que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param organId
	 * @param id
	 *            Informació del meta-expedient a marcar com a pendent.
	 * @return El meta-expedient modificat.
	 * @throws NotFoundException TODO
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	public MetaExpedientDto marcarProcesDisseny(
			Long entitatId, 
			Long id, 
			Long organId);
	
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('IPA_REVISIO')")
	public boolean publicarComentariPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String text,
			String rolActual);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('IPA_REVISIO')")
	public List<MetaExpedientComentariDto> findComentarisPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String rolActual);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('IPA_REVISIO')")
	public String export(
			Long entitatId,
			Long id,
			Long organActualId);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('IPA_REVISIO')")
	public void createFromImport(
			Long entitatId,
			MetaExpedientExportDto metaExpedient,
			String rolActual,
			Long organId);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN') or hasRole('IPA_REVISIO')")
	public CrearReglaResponseDto reintentarCreacioReglaDistribucio(
			Long entitatId,
			Long metaExpedientId);

	/**
	 * Consulta si existeix un procés en curs actualitzant els procediments de l'entitat indicada.
	 *
	 * @param entitatDto Entitat que es vol consultar
	 * @return boolean indicant si existeix un procés en segon pla actualitzant els procediements de l'entitat indicada.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	boolean isUpdatingProcediments(EntitatDto entitatDto);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	ProgresActualitzacioDto getProgresActualitzacio(String codi);

	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_ORGAN_ADMIN')")
	void actualitzaProcediments(EntitatDto entitat, Locale locale);

	public Integer getMetaExpedientsAmbOrganNoSincronitzat(Long entitatId);
}
