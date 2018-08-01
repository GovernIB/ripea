/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a gestionar continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatOrganitzativaService {

	/**
	 * Consulta les unitats organitzatives de l'entitat.
	 * 
	 * @param entitatCodi
	 *            Atribut codi de l'entitat a la qual pertany l'interessat.
	 * @return La llista d'unitats organitzatives.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi);

	/**
	 * Consulta una unitat organitzativa donat el seu codi.
	 * 
	 * @param codi
	 *            Codi DIR3 de la unitat organitzativa.
	 * @return La unitat organitzativa trobada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb el codi especificat.
	 */
	public UnitatOrganitzativaDto findByCodi(
			String codi);

	/**
	 * Consulta les unitats organitzatives segons el filtre.
	 * 
	 * @param codiDir3
	 *            Codi DIR3 de la unitat organitzativa.
	 * @param denominacio
	 *            Denominació de la unitat organitzativa.
	 * @param nivellAdministracio
	 *            Nivel de l'administració.
	 * @param comunitatAutonoma
	 *            Valor del paràmetre comunitatAutonoma.
	 * @param provincia
	 *            Valor del paràmetre província.
	 * @param municipi
	 *            Valor del paràmetre municipi.
	 * @param arrel
	 *            Indica si s'ha de consultar únicament les unitats arrel.
	 *            Atribut codi de l'unitat.
	 * @return La llista d'unitats organitzatives que compleixen el filtre.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio, 
			String nivellAdm,
			String comunitat, 
			String provincia, 
			String localitat, 
			Boolean arrel);

}
