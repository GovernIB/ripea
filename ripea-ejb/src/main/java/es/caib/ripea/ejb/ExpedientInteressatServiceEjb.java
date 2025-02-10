/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.InteressatAdministracioDto;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ExpedientInteressatService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de InteressatService com a EJB que empra una clase delegada per
 * accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientInteressatServiceEjb implements ExpedientInteressatService {

	@Delegate
	private ExpedientInteressatService delegateService;

	protected void setDelegateService(ExpedientInteressatService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto create(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) {
		return delegateService.create(entitatId, expedientId, interessat, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			boolean propagarArxiu, String rolActual) {
		return delegateService.create(entitatId, expedientId, interessatId, representant, propagarArxiu, null);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto update(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) {
		return delegateService.update(entitatId, expedientId, interessat, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto update(Long entitatId, Long expedientId, Long interessatId, InteressatDto representant, String rolActual) {
		return delegateService.update(entitatId, expedientId, interessatId, representant, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void delete(Long entitatId, Long expedientId, Long interessatId, String rolActual) {
		delegateService.delete(entitatId, expedientId, interessatId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void deleteRepresentant(Long entitatId, Long expedientId, Long interessatId, Long representantId, String rolActual) {
		delegateService.deleteRepresentant(entitatId, expedientId, interessatId, representantId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findById(Long id, boolean consultarDadesExternes) {
		return delegateService.findById(id, consultarDadesExternes);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findRepresentantById(Long interessatId, Long id) {
		return delegateService.findRepresentantById(interessatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public long countByExpedient(Long entitatId, Long expedientId) {
		return delegateService.countByExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findAmbDocumentPerNotificacio(Long entitatId, Long documentId) {
		return delegateService.findAmbDocumentPerNotificacio(entitatId, documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatPersonaFisicaDto> findByFiltrePersonaFisica(
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			Long expedientId) {
		return delegateService.findByFiltrePersonaFisica(documentNum, nom, llinatge1, llinatge2, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatPersonaJuridicaDto> findByFiltrePersonaJuridica(
			String documentNum,
			String raoSocial,
			Long expedientId) {
		return delegateService.findByFiltrePersonaJuridica(documentNum, raoSocial, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatAdministracioDto> findByFiltreAdministracio(String organCodi, Long expedientId) {
		return delegateService.findByFiltreAdministracio(organCodi, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findByExpedient(
			Long entitatId,
			Long expedientId,
			boolean nomesAmbNotificacioActiva) throws NotFoundException {
		return delegateService.findByExpedient(entitatId, expedientId, nomesAmbNotificacioActiva);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findByExpedientAndDocumentNum(
			String documentNum,
			Long expedientId) throws NotFoundException {
		return delegateService.findByExpedientAndDocumentNum(documentNum, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findByText(String text) {
		return delegateService.findByText(text);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findByDocumentNum(String documentNum) throws NotFoundException {
		return delegateService.findByDocumentNum(documentNum);
	}

	@Override
	@RolesAllowed("tothom")
	public Exception guardarInteressatsArxiu(Long expId) {
		return delegateService.guardarInteressatsArxiu(expId);
	}

	@Override
	@RolesAllowed("tothom")
	public Long findExpedientIdByInteressat(Long interessatId) {
		return delegateService.findExpedientIdByInteressat(interessatId);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto createRepresentant(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessat,
			boolean propagarArxiu,
			String rolActual) {
		return delegateService.createRepresentant(
				entitatId,
				expedientId,
				interessatId,
				interessat,
				propagarArxiu,
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public String importarInteressats(Long entitatId, Long expedientId, String rolActual, List<InteressatDto> interessats, List<Long> seleccionats) throws NotFoundException {
		return delegateService.importarInteressats(entitatId, expedientId, rolActual, interessats, seleccionats);
	}
}