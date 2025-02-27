/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió dels dominis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiService {

	/**
	 * Crea un nou domini.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param domini
	 *            Informació del domini a crear;
	 * @return El domini creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public DominiDto create(
			Long entitatId,
			DominiDto domini) throws NotFoundException;

	/**
	 * Actualitza la informació del domini que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param domini
	 *            Informació del domini a modificar.
	 * @return El domini modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public DominiDto update(
			Long entitatId,
			DominiDto tipusDocumental) throws NotFoundException;

	/**
	 * Esborra el domini amb el mateix id que l'especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del domini a esborrar.
	 * @return El domini esborrat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public DominiDto delete(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Consulta un domini donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id del domini a cercar.
	 * @return El domini amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY')")
	public DominiDto findById(
			Long entitatId,
			Long id) throws NotFoundException;
	
	/**
	 * Llistat paginat amb tots els dominis de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista dels dominis.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY')")
	public PaginaDto<DominiDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	/**
	 * Llistat amb tots els dominis de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de dominis.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<DominiDto> findByEntitat(
			Long entitatId) throws NotFoundException;

	/**
	 * Recupera un domini d'una entitat a partir del seu codi.
	 * 
	 * @param codi
	 *            Codi del domini.
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return El domini.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public DominiDto findByCodiAndEntitat(
			String codi,
			Long entitatId) throws NotFoundException;
	
	/**
	 * Recupera el resultat de una consulta d'un domini.
	 * 
	 * @param entitatId
	 *            	Id de l'entitat.
	 * @param domini 
	 * 				Informació del domini.
	 * @param currentLength
	 * 				Longitud actual de la consulta de dominis
	 * @param currentLength 
	 * @return Resultat de la consulta.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public ResultatDominiDto getResultDomini(
			Long entitatId,
			DominiDto domini,
			String filter,
			int page,
			int resultCount) throws NotFoundException;
	
	/**
	 * Recupera el resultat de una consulta d'un domini.
	 * 
	 * @param entitatId
	 *            	Id de l'entitat.
	 * @param domini 
	 * 				Informació del domini.
	 * @param currentLength
	 * 				Longitud actual de la consulta de dominis
	 * @param currentLength 
	 * @return Resultat de la consulta.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public ResultatConsultaDto getSelectedDomini(
			Long entitatId,
			DominiDto domini,
			String dadaValor) throws NotFoundException;
	
	/**
	 * Recupera el resultat de una consulta d'un domini.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param domini 
	 * 				Informació del domini.
	 * @return Resultat de la consulta.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<DominiDto> findByMetaNodePermisLecturaAndTipusDomini(
			Long entitatId, 
			MetaExpedientDto metaExpedient);
	
	/**
	 * Buida la cache dels dominis.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param domini 
	 * 				Informació del domini.
	 * @return Resultat de la consulta.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY')")
	public void evictDominiCache();
}
