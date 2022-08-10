package es.caib.ripea.core.api.service;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.OrganGestorFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermisOrganGestorDto;
import es.caib.ripea.core.api.dto.PrediccioSincronitzacio;
import es.caib.ripea.core.api.dto.ProgresActualitzacioDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Locale;

/**
 * Declaració dels mètodes per a la gestió de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorService {

	/**
	 * Obté una llista amb tots els organs gestors de la base de dades
	 * 
	 * @return Llistat de tots els organs gestors
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<OrganGestorDto> findAll();
	
	/**
	 * Obté l'element amb l'identificador donat.
	 * 
	 * @param id Identificador de l'element a consultar
	 * 
	 * @return L'objecte del registre consultat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public OrganGestorDto findItem(Long id) throws NotFoundException;
	
	/**
	 * Obté una llista amb tots els organs gestors de l'entitat especificada
	 * per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return Llistat dels organs gestors de l'entitat
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findByEntitat(Long entitatId);

	/**
	 * Obté una llista amb tots els organs gestors de l'entitat especificada
	 * per paràmetre. 
	 * 
	 * Selecciona les que tenen el patró espedificat al parametre de filtre al nom o al codi.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filter
	 *            Paràmetre de filtre 
	 * @return Llistat dels organs gestors de l'entitat
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findByEntitat(Long entitatId, String filter);
	
	/**
	 * Consulta tots els organs gestors de l'entitat actual de forma paginada 
	 * i aplicant el filtre.
	 * 
	 * @param entitatId Identificador de l'entitat actual
	 * @param filtre TODO
	 * @param paginacioParams
	 * 		Paràmetres per a dur a terme la paginació del resultats.
	 * @param filtre Filtre a aplicar als resultats
	 * @return La pàgina amb els organs gestors
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId,
			OrganGestorFiltreDto filtre, PaginacioParamsDto paginacioParams);

	/**
	 * Actualitza els organs gestors de la base de dades amb els de Dir3
	 * 
	 * @param entitat Identificador de l'entitat actual
	 * @return Indica si la sincronització ha tengut èxit
	 * @throws Exception 
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public Object[] syncDir3OrgansGestors(EntitatDto entitat, Locale locale) throws Exception;

	@PreAuthorize("hasRole('IPA_ADMIN')")
	PrediccioSincronitzacio predictSyncDir3OrgansGestors(Long entitatId) throws Exception;

	@PreAuthorize("hasRole('IPA_ADMIN')")
	ProgresActualitzacioDto getProgresActualitzacio(String entitatCodi);


	/**
	 * Obté un llistat de tots els organs gestors accessibles per a 
	 * l'usuari actual de l'entitat indicada per paràmetre
	 * 
	 * @param entitatId
	 *            Id de l'entitat actual.
	 * @return
	 * 			  Llistat de tots els organs gestors accesssibles per a l'entitat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdmin(Long entitatId, Long organGestorId);
		
	/**
	 * Obté un llistat de tots els organs gestors accessibles per a 
	 * l'usuari actual de l'entitat indicada per paràmetre
	 * Selecciona les que tenen el patró espedificat al parametre de filtre al nom o al codi.
	 *
	 * @param entitatId
	 *            Id de l'entitat actual.
	 * @param filter
	 *            Paràmetre de filtre 
	 * @return
	 * 		      Llistat de tots els organs gestors accesssibles per a l'entitat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdmin(
			Long entitatId,
			Long organGestorId,
			String filter);

	/**
	 * Obté un llistat de tots els organs gestors de l'entitat actual pels quals es té algún permís
	 * 
	 * @param entitatId
	 *            Id de l'entitat actual.
	 * @return
	 * 		      Llistat de tots els organs gestors de l'entitat amb permisos.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermis(Long entitatId);

	/**
	 * Obté un llistat de tots els organs gestors de l'entitat actual pels quals es té algún permís
	 * 
	 * @param entitatId
	 *            Id de l'entitat actual.
	 * @param metaExpedientId
	 *            Id del meta-expedient.
	 * @param filter
	 *            text per a filtrar els resultats. 
	 * @param expedientId TODO
	 * @param rolActual
	 * 			  boolean que indica si el rol actual és Admin d'entitat
	 * @param organActualId TODO
	 * @return
	 * 		      Llistat de tots els organs gestors de l'entitat amb permisos.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			String filter, 
			Long expedientId,
			String rolActual, Long organActualId);

	/**
	 * Consulta els permisos dels distits organs gestors de l'entitat
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual es volen consultar els permisos.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<PermisOrganGestorDto> findPermisos(Long entitatId) throws NotFoundException;

	/**
	 * Consulta els permisos dels distits organs gestors de l'entitat
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual es volen consultar els permisos.
	 * @param organId
	 *            Atribut id de l'organ gestor del qual es volen consultar els permisos.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<PermisOrganGestorDto> findPermisos(Long entitatId, Long organId) throws NotFoundException;
	
	/**
	 * Modifica els permisos d'un usuari o d'un rol per a un organ gestor.
	 * 
	 * @param id
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @param entitatId
	 *            Id de l'entitat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void updatePermis(
			Long id,
			PermisDto permis,
			Long entitatId) throws NotFoundException;

	/**
	 * Esborra els permisos d'un usuari o d'un rol per a una entitat com a
	 * administrador de l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'organ gestorde la qual es vol modificar el permís.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @param entitatId
	 *            Id de l'entitat actual.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void deletePermis(
			Long id,
			Long permisId,
			Long entitatId) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findAccessiblesUsuariActualRolUsuari(
			Long entitatId,
			String filter, 
			boolean directOrganPermisRequired);

	@PreAuthorize("hasRole('tothom')")
	public OrganGestorDto findItemByEntitatAndCodi(
			Long entitatId,
			String codi);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public OrganGestorDto findById(
			Long entitatId,
			Long id);
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public OrganGestorDto create(
			Long entitatId,
			OrganGestorDto organGestorDto);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public OrganGestorDto update(
			Long entitatId,
			OrganGestorDto organGestorDto);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void delete(
			Long entitatId,
			Long id);

	@PreAuthorize("hasRole('tothom')")
    boolean hasPermisAdminComu(Long organId);
	
	@PreAuthorize("hasRole('tothom')")
	public void evictOrganismesEntitatAmbPermis(
			Long entitatId,
			String usuariCodi);

	@PreAuthorize("hasRole('tothom')")
	public List<OrganGestorDto> findOrganismesEntitatAmbPermisCache(
			Long entitatId);
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<OrganGestorDto> findOrgansSuperiorByEntitat(
			Long entitatId);



	// For testing:
	public void setServicesForSynctest(Object metaExpedientHelper, Object pluginHelper);
}
