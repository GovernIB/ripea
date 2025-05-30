/**
 * 
 */
package es.caib.ripea.ejb;

import java.io.InputStream;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.InteressatAdministracioDto;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ExpedientInteressatService;
import lombok.experimental.Delegate;

@Stateless
public class ExpedientInteressatServiceEjb extends AbstractServiceEjb<ExpedientInteressatService> implements ExpedientInteressatService {

	@Delegate private ExpedientInteressatService delegateService;

	protected void setDelegateService(ExpedientInteressatService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto create(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) {
		return delegateService.create(entitatId, expedientId, interessat, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			boolean propagarArxiu, String rolActual) {
		return delegateService.create(entitatId, expedientId, interessatId, representant, propagarArxiu, null);
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto update(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) {
		return delegateService.update(entitatId, expedientId, interessat, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto update(Long entitatId, Long expedientId, Long interessatId, InteressatDto representant, String rolActual) {
		return delegateService.update(entitatId, expedientId, interessatId, representant, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public void delete(Long entitatId, Long expedientId, Long interessatId, String rolActual) {
		delegateService.delete(entitatId, expedientId, interessatId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public void deleteRepresentant(Long entitatId, Long expedientId, Long interessatId, Long representantId, String rolActual) {
		delegateService.deleteRepresentant(entitatId, expedientId, interessatId, representantId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto findById(Long id, boolean consultarDadesExternes) {
		return delegateService.findById(id, consultarDadesExternes);
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto findRepresentantById(Long interessatId, Long id) {
		return delegateService.findRepresentantById(interessatId, id);
	}

	@Override
	@RolesAllowed("**")
	public long countByExpedient(Long entitatId, Long expedientId) {
		return delegateService.countByExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<InteressatDto> findAmbDocumentPerNotificacio(Long entitatId, Long documentId) {
		return delegateService.findAmbDocumentPerNotificacio(entitatId, documentId);
	}

	@Override
	@RolesAllowed("**")
	public List<InteressatPersonaFisicaDto> findByFiltrePersonaFisica(
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			Long expedientId) {
		return delegateService.findByFiltrePersonaFisica(documentNum, nom, llinatge1, llinatge2, expedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<InteressatPersonaJuridicaDto> findByFiltrePersonaJuridica(
			String documentNum,
			String raoSocial,
			Long expedientId) {
		return delegateService.findByFiltrePersonaJuridica(documentNum, raoSocial, expedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<InteressatAdministracioDto> findByFiltreAdministracio(String organCodi, Long expedientId) {
		return delegateService.findByFiltreAdministracio(organCodi, expedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<InteressatDto> findByExpedient(
			Long entitatId,
			Long expedientId,
			boolean nomesAmbNotificacioActiva) throws NotFoundException {
		return delegateService.findByExpedient(entitatId, expedientId, nomesAmbNotificacioActiva);
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto findByExpedientAndDocumentNum(
			String documentNum,
			Long expedientId) throws NotFoundException {
		return delegateService.findByExpedientAndDocumentNum(documentNum, expedientId);
	}

	@Override
	@RolesAllowed("**")
	public List<InteressatDto> findByText(String text) {
		return delegateService.findByText(text);
	}

	@Override
	@RolesAllowed("**")
	public InteressatDto findByDocumentNum(String documentNum) throws NotFoundException {
		return delegateService.findByDocumentNum(documentNum);
	}

	@Override
	@RolesAllowed("**")
	public Exception guardarInteressatsArxiu(Long expId) {
		return delegateService.guardarInteressatsArxiu(expId);
	}

	@Override
	@RolesAllowed("**")
	public Long findExpedientIdByInteressat(Long interessatId) {
		return delegateService.findExpedientIdByInteressat(interessatId);
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public String importarInteressats(Long entitatId, Long expedientId, String rolActual, List<InteressatDto> interessats, List<Long> seleccionats) throws NotFoundException {
		return delegateService.importarInteressats(entitatId, expedientId, rolActual, interessats, seleccionats);
	}
	
	@Override
	@RolesAllowed("**")
	public List<InteressatDto> extreureInteressatsExcel(InputStream inputStream) {
		return delegateService.extreureInteressatsExcel(inputStream);
	}
	
	@Override
	@RolesAllowed("**")
	public byte[] getModelDadesInteressatsExcel() {
		return delegateService.getModelDadesInteressatsExcel();
	}
	
}