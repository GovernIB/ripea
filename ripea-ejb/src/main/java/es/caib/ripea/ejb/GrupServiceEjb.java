package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.GrupFiltreDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.ResultDto;
import es.caib.ripea.service.intf.dto.ResultEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.GrupService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class GrupServiceEjb extends AbstractServiceEjb<GrupService> implements GrupService {

	@Delegate private GrupService delegateService;

	protected void setDelegateService(GrupService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public GrupDto create(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegateService.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto update(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegateService.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto findById(
			Long id) throws NotFoundException {
		return delegateService.findById(
				id);
	}
	
	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public void desvincularAmbMetaExpedient(Long entitatId,
			Long metaExpedientId,
			Long id, String rolActual, Long organId) {
		delegateService.desvincularAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id, rolActual, organId);
	}

	@Override
	@RolesAllowed("**")
	public List<PermisDto> findPermisos(
			Long id) {
		return delegateService.findPermisos(id);
	}

	@Override
	@RolesAllowed("**")
	public void updatePermis(
			Long id,
			PermisDto permis) {
		delegateService.updatePermis(id, permis);
	}

	@Override
	@RolesAllowed("**")
	public void deletePermis(
			Long id,
			Long permisId) {
		delegateService.deletePermis(id, permisId);
	}
    
	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public GrupDto findGrupById(Long grupId) {
		return delegateService.findGrupById(grupId);
	}

	@Override
	@RolesAllowed("**")
	public GrupDto findGrupByExpedientPeticioAndProcedimentId(
			Long expedientPeticioId,
			Long procedimentId) {
		return delegateService.findGrupByExpedientPeticioAndProcedimentId(
				expedientPeticioId,
				procedimentId);
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public boolean checkIfHasGrupPerDefecte(Long procedimentId) {
		return delegateService.checkIfHasGrupPerDefecte(procedimentId);
	}
}