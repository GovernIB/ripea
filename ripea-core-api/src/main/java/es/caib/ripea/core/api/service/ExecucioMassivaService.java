/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.List;

import es.caib.ripea.core.api.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;

/**
 * Declaració dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaService {
	
	/**
	 * Crea una nova execució massiva
	 * 
	 * @param dto
	 *            Dto amb la informació de l'execució massiva a programar
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException, ValidationException;

	@PreAuthorize("hasRole('tothom')")
	public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execMassivaId);

	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException;
	
	public List<ExecucioMassivaDto> findExecucionsMassivesGlobals() throws NotFoundException;
	
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException;

	public void executeNextMassiveScheduledTask();

	@PreAuthorize("hasRole('tothom')")
	public void saveExecucioMassiva(
			Long entitatId,
			ExecucioMassivaDto dto,
			List<ExecucioMassivaContingutDto> exc,
			ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException;

}
