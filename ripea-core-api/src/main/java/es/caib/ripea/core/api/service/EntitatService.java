/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatService {

	/**
	 * Crea una nova entitat.
	 * 
	 * @param entitat
	 *            Informació de l'entitat a crear.
	 * @return L'Entitat creada.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public EntitatDto create(EntitatDto entitat);

	/**
	 * Actualitza la informació de l'entitat que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitat
	 *            Informació de l'entitat a modificar.
	 * @return L'entitat modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public EntitatDto update(
			EntitatDto entitat) throws NotFoundException;

	/**
	 * Marca l'entitat amb l'id especificat com a activa/inactiva.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a esborrar.
	 * @param activa
	 *            true si es vol activar o false en cas contrari.
	 * @return L'entitat modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public EntitatDto updateActiva(
			Long id,
			boolean activa) throws NotFoundException;

	/**
	 * Esborra l'entitat amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a esborrar.
	 * @return L'entitat esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public EntitatDto delete(
			Long id) throws NotFoundException;

	/**
	 * Consulta una entitat donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a trobar.
	 * @return L'entitat amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public EntitatDto findById(
			Long id) throws NotFoundException;

	/**
	 * Consulta una entitat donat el seu codi.
	 * 
	 * @param codi
	 *            Atribut codi de l'entitat a trobar.
	 * @return L'entitat amb el codi especificat o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public EntitatDto findByCodi(String codi);

	/**
	 * Llistat amb totes les entitats paginades.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'Entitats.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public PaginaDto<EntitatDto> findPaginat(PaginacioParamsDto paginacioParams);

	/**
	 * Llistat amb les entitats accessibles per a l'usuari actual.
	 * 
	 * @return El llistat d'entitats.
	 */
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	public List<EntitatDto> findAccessiblesUsuariActual();

	/**
	 * Consulta els permisos de l'entitat com a superusuari.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a la qual es volen consultar els permisos.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public List<PermisDto> findPermisSuper(
			Long id) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a una entitat com a
	 * superusuari.
	 * 
	 * @param id
	 *            Atribut id de l'entitat de la qual es modificar el permís.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public void updatePermisSuper(
			Long id,
			PermisDto permis) throws NotFoundException;

	/**
	 * Esborra els permisos d'un usuari o d'un rol per a una entitat com a
	 * superusuari.
	 * 
	 * @param id
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_SUPER')")
	public void deletePermisSuper(
			Long id,
			Long permisId) throws NotFoundException;

	/**
	 * Consulta els permisos de l'entitat com a administrador.
	 * 
	 * @param id
	 *            Atribut id de l'entitat a la qual es volen consultar els permisos.
	 * @return El llistat de permisos.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public List<PermisDto> findPermisAdmin(
			Long id) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a una entitat com a
	 * administrador de l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void updatePermisAdmin(
			Long id,
			PermisDto permis) throws NotFoundException;

	/**
	 * Esborra els permisos d'un usuari o d'un rol per a una entitat com a
	 * administrador de l'entitat.
	 * 
	 * @param id
	 *            Atribut id de l'entitat de la qual es vol modificar el permís.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void deletePermisAdmin(
			Long id,
			Long permisId) throws NotFoundException;

	@PreAuthorize("hasRole('IPA_ADMIN')")
	byte[] getLogo() throws NoSuchFileException, IOException;

	@PreAuthorize("hasRole('tothom')")
	void evictEntitatsAccessiblesUsuari();

	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN') or hasRole('tothom')")
	EntitatDto findByUnitatArrel(String unitatArrel);
	
	@PreAuthorize("hasRole('IPA_SUPER') or hasRole('IPA_ADMIN')")
	List<EntitatDto> findAll();

}
