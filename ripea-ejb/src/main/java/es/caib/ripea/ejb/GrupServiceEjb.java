package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.GrupService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class GrupServiceEjb implements GrupService {

	@Delegate
	private GrupService delegateService;

	protected void setDelegateService(GrupService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public GrupDto create(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegateService.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	public GrupDto update(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegateService.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	public GrupDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.delete(
				entitatId, 
				id);
	}

	@Override
	public GrupDto findById(
			Long id) throws NotFoundException {
		return delegateService.findById(
				id);
	}
	
	@Override
	public ResultDto<GrupDto> findByEntitat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams, 
			Long organId, 
			GrupFiltreDto filtre,
			ResultEnumDto resultEnum)
			throws NotFoundException {
		return delegateService.findByEntitat(
				entitatId, 
				metaExpedientId, 
				paginacioParams, 
				organId, 
				filtre, 
				resultEnum);
	}
	
	@Override
	public PaginaDto<GrupDto> findByEntitatPaginat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams, 
			Long organId)
			throws NotFoundException {
		return delegateService.findByEntitatPaginat(
				entitatId, 
				metaExpedientId, 
				paginacioParams, 
				organId);
	}

	@Override
	public void relacionarAmbMetaExpedient(Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId, 
			boolean marcarPerDefecte) {
		delegateService.relacionarAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id,
				rolActual,
				organId,
				marcarPerDefecte);

	}

	@Override
	public void desvincularAmbMetaExpedient(Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId) {
		delegateService.desvincularAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id, rolActual, organId);
	}

	@Override
	public List<PermisDto> findPermisos(
			Long id) {
		return delegateService.findPermisos(id);
	}

	@Override
	public void updatePermis(
			Long id,
			PermisDto permis) {
		delegateService.updatePermis(id, permis);
	}

	@Override
	public void deletePermis(
			Long id,
			Long permisId) {
		delegateService.deletePermis(id, permisId);
	}
    
	@Override
	public boolean checkIfAlreadyExistsWithCodi(
			Long entitatId,
			String codi, 
			Long grupId) {
		return delegateService.checkIfAlreadyExistsWithCodi(
				entitatId,
				codi, 
				grupId);
	}
    
	@Override
	public void marcarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId) {
		delegateService.marcarPerDefecte(
				entitatId,
				procedimentId,
				grupId);
	}

	@Override
	public List<GrupDto> findGrupsNoRelacionatAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long organGestorId) {
		return delegateService.findGrupsNoRelacionatAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				organGestorId);
	}

	@Override
	public void esborrarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId) {
		delegateService.esborrarPerDefecte(
				entitatId,
				procedimentId,
				grupId);
	}

	@Override
	public List<GrupDto> findGrups(
			Long entitatId,
			Long organGestorId,
			Long metaExpedientId) {
		return delegateService.findGrups(
				entitatId,
				organGestorId,
				metaExpedientId);
	}

	@Override
	public GrupDto findGrupById(Long grupId) {
		return delegateService.findGrupById(grupId);
	}

	@Override
	public GrupDto findGrupByExpedientPeticioAndProcedimentId(
			Long expedientPeticioId,
			Long procedimentId) {
		return delegateService.findGrupByExpedientPeticioAndProcedimentId(
				expedientPeticioId,
				procedimentId);
	}

	@Override
	public List<GrupDto> findGrupsPermesosProcedimentsGestioActiva(
			Long entitatId,
			String rolActual,
			Long organGestorId) {
		return delegateService.findGrupsPermesosProcedimentsGestioActiva(
				entitatId,
				rolActual,
				organGestorId);
	}

	@Override
	public boolean checkIfHasGrupPerDefecte(Long procedimentId) {
		return delegateService.checkIfHasGrupPerDefecte(procedimentId);
	}
}