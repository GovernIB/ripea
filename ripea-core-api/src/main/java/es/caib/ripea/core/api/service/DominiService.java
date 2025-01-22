/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ResultatConsultaDto;
import es.caib.ripea.core.api.dto.ResultatDominiDto;
import es.caib.ripea.core.api.exception.NotFoundException;

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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY')")
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
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_DISSENY') or hasRole('IPA_ORGAN_ADMIN') or hasRole('tothom')")
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
	@PreAuthorize("hasRole('tothom')")
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
	@PreAuthorize("hasRole('tothom')")
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
	@PreAuthorize("hasRole('tothom')")
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
	@PreAuthorize("hasRole('tothom')")
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
