/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
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
	 * @throws SQLException 
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
	public PaginaDto<MetaExpedientDto> findByEntitat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

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
	 * @return El meta-expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca) throws NotFoundException;

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
	 * @return La tasca del meta-expedient modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientTascaDto tascaUpdate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca) throws NotFoundException;

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
	 * @return El meta-expedient modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientTascaDto tascaUpdateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean activa) throws NotFoundException;

	/**
	 * Esborra la tasca del meta-expedient amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param id
	 *            Id de la tasca del meta-expedient a esborrar.
	 * @return La tasca del meta-expedient esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public MetaExpedientTascaDto tascaDelete(
			Long entitatId,
			Long metaExpedientId,
			Long id) throws NotFoundException;

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
	@PreAuthorize("hasRole('IPA_ADMIN')")
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
	@PreAuthorize("hasRole('IPA_ADMIN')")
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
	@PreAuthorize("hasRole('IPA_ADMIN')")
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
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void permisUpdate(
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
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException;

	 /**
	  * Consulta els meta-expedients d'una entitat que tenen algún organ gestor asociat de forma paginada.
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
	public PaginaDto<MetaExpedientDto> findAmbOrganGestor(
			Long entitatId,
			PaginacioParamsDto paginacioParams);

}
