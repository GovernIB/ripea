/**
 * 
 */
package es.caib.ripea.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió d'alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AlertaService {

	/**
	 * Crea una nova alerta.
	 * 
	 * @param alerta
	 *            Informació de l'alerta a crear.
	 * @return L'Alerta creada.
	 */
	public AlertaDto create(AlertaDto alerta);

	/**
	 * Actualitza la informació de l'alerta que tengui el mateix
	 * id que l'especificat per paràmetre.
	 * 
	 * @param alerta
	 *            Informació de l'alerta a modificar.
	 * @return L'alerta modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public AlertaDto update(
			AlertaDto alerta) throws NotFoundException;
	
	/**
	 * Esborra l'alerta amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id de l'alerta a esborrar.
	 * @return L'alerta esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public AlertaDto delete(
			Long id) throws NotFoundException;
	
	/**
	 * Cerca l'alerta amb el mateix id que l'especificat.
	 * 
	 * @param id
	 *            Atribut id de l'alerta a trobar.
	 * @return L'alerta.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public AlertaDto find(
			Long id);

	/**
	 * Llistat amb totes les alertes paginades.
	 * 
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina d'Alertes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<AlertaDto> findPaginat(PaginacioParamsDto paginacioParams);

	/**
	 * Llistat amb les alertes llegides pertanyents a un contingut.
	 * 
	 * @param llegida Atribut de cerca en funció de si es volen trobar les alertes llegides.
	 * @param contingutId Identificador del contingut del qual es volen trobar alertes.
	 * 
	 * @return El llistat d'alertes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<AlertaDto> findPaginatByLlegida(
			boolean llegida,
			Long contingutId,
			PaginacioParamsDto paginacioParams);

}
