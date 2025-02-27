/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.ExpedientTascaService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.List;

/**
 * Implementació de ExpedientTascaService com a EJB que empra una clase delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientTascaServiceEjb implements ExpedientTascaService {

	@Delegate
	private ExpedientTascaService delegateService;

	protected void setDelegateService(ExpedientTascaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public ExpedientTascaDto findOne(Long expedientPeticioId) {
		return delegateService.findOne(expedientPeticioId);
	}

	@Override
	@RolesAllowed("**")
	public List<ExpedientTascaDto> findAmbExpedient(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParam) {
		return delegateService.findAmbExpedient(entitatId, expedientId, paginacioParam);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaExpedientTascaDto> findAmbMetaExpedient(Long entitatId,
			Long metaExpedientId) {
		return delegateService.findAmbMetaExpedient(entitatId,
				metaExpedientId);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca) {
		return delegateService.createTasca(
				entitatId,
				expedientId,
				expedientTasca);
	}

	@Override
	@RolesAllowed("**")
	public MetaExpedientTascaDto findMetaExpedientTascaById(Long metaExpedientTascaId) {
		return delegateService.findMetaExpedientTascaById(metaExpedientTascaId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<ExpedientTascaDto> findAmbAuthentication(
			Long entitatId,
			UsuariTascaFiltreDto filtre,
			PaginacioParamsDto paginacioParam) {
		return delegateService.findAmbAuthentication(
				entitatId,
				filtre,
				paginacioParam);
	}

	@Override
	@RolesAllowed("**")
	public long countTasquesPendents() {
		return delegateService.countTasquesPendents();
	}


	@Override
	@RolesAllowed("**")
	public ContingutDto findTascaExpedient(Long entitatId,
	                                       Long contingutId,
	                                       Long tascaId,
	                                       boolean ambFills,
	                                       boolean ambVersions) {
		return delegateService.findTascaExpedient(entitatId,
				contingutId,
				tascaId,
				ambFills,
				ambVersions);
	}


	@Override
	@RolesAllowed("**")
	public void deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException {
		delegateService.deleteTascaReversible(
				entitatId,
				tascaId,
				contingutId);
	}


	@Override
	public ExpedientTascaDto canviarTascaEstat(
			Long expedientTascaId,
			TascaEstatEnumDto tascaEstatEnumDto,
			String motiu, 
			String rolActual) {
		return delegateService.canviarTascaEstat(
				expedientTascaId,
				tascaEstatEnumDto,
				motiu, 
				rolActual);
	}
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, 
			List<String> responsablesCodi) {
		return delegateService.updateResponsables(
				expedientTascaId,
				responsablesCodi);
	}
		
	@Override
	public List<MetaExpedientTascaDto> findAmbEntitat(Long entitatId) {
		return delegateService.findAmbEntitat(entitatId);
	}

    @Override
    public boolean publicarComentariPerExpedientTasca(Long entitatId, Long expedientTascaId, String text, String rolActual) {
        return delegateService.publicarComentariPerExpedientTasca(entitatId, expedientTascaId, text, rolActual);
    }

	@Override
	public List<ExpedientTascaComentariDto> findComentarisPerTasca(Long entitatId, Long expedientTascaId) {
		return delegateService.findComentarisPerTasca(entitatId, expedientTascaId);
	}

	@Override
	@RolesAllowed("**")
	public ContingutDto findByTascaBasicInfo(Long contingutId, Long tascaId) {
		return delegateService.findByTascaBasicInfo(contingutId, tascaId);
	}

	@Override
	public ExpedientTascaDto updateDataLimit(ExpedientTascaDto expedientTascaDto) {
		return delegateService.updateDataLimit(expedientTascaDto);
	}

	@Override
	@RolesAllowed("**")
	public void changeTascaPrioritat(ExpedientTascaDto expedientTascaDto) {
		delegateService.changeTascaPrioritat(expedientTascaDto);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientTascaDto updateDelegat(Long expedientTascaId, String delegatCodi, String comentari) {
		return delegateService.updateDelegat(expedientTascaId, delegatCodi, comentari);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientTascaDto cancelarDelegacio(Long expedientTascaId, String comentari) {
		return delegateService.cancelarDelegacio(expedientTascaId, comentari);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientTascaDto reobrirTasca(Long expedientTascaId, List<String> responsablesCodi, String motiu,
			String rolActual) {
		return delegateService.reobrirTasca(expedientTascaId, responsablesCodi, motiu, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaExpedientTascaValidacioDto> getValidacionsPendentsTasca(Long expedientTascaId) {
		return delegateService.getValidacionsPendentsTasca(expedientTascaId);
	}
}