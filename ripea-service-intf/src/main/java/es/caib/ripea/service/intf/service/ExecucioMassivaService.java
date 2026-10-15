/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import javax.annotation.security.PermitAll;

/**
 * Declaració dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException, ValidationException;

	@PreAuthorize("isAuthenticated()")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto, ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException;
	
	@PreAuthorize("isAuthenticated()")
	public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execMassivaId);

	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException;
	
	public List<ExecucioMassivaDto> findExecucionsMassivesGlobals() throws NotFoundException;
	
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException;

	@PermitAll
	public void executeNextMassiveScheduledTask();

	@PreAuthorize("isAuthenticated()")
	public void saveExecucioMassiva(
			Long entitatId,
			ExecucioMassivaDto dto,
			List<ExecucioMassivaContingutDto> exc,
			ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException;

	/**
	 * Cancel·la una execució massiva. Només ho pot fer el creador o un administrador de l'entitat.
	 * Si encara no ha començat, es tanca a l'acte. Si està en curs, l'element que s'està processant
	 * acaba normalment i la resta queden cancel·lats en acabar-lo.
	 *
	 * @param rolActual rol actual de l'usuari a la interfície.
	 * @return CANCELADA si s'ha tancat a l'acte, o EN_CURS si s'ha sol·licitat la cancel·lació.
	 * @throws ValidationException si el tipus no es pot cancel·lar o l'execució ja ha acabat.
	 */
	@PreAuthorize("isAuthenticated()")
	public ExecucioMassivaProcesEstatDto cancelarExecucioMassiva(
			Long entitatId,
			Long execucioMassivaId,
			String rolActual) throws NotFoundException, ValidationException;

}
