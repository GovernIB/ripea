/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.AlertaDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

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
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<AlertaDto> findPaginat(PaginacioParamsDto paginacioParams);

}
