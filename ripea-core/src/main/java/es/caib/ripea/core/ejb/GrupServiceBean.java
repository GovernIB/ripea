package es.caib.ripea.core.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.GrupFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.GrupService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class GrupServiceBean implements GrupService {

    @Autowired private GrupService delegate;
    
	@Override
	public GrupDto create(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegate.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	public GrupDto update(
			Long entitatId, 
			GrupDto tipusDocumental) throws NotFoundException {
		return delegate.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	public GrupDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	public GrupDto findById(
			Long id) throws NotFoundException {
		return delegate.findById(
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
		return delegate.findByEntitat(
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
		return delegate.findByEntitatPaginat(
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
		delegate.relacionarAmbMetaExpedient(
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
		delegate.desvincularAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				id, rolActual, organId);
	}

	@Override
	public List<PermisDto> findPermisos(
			Long id) {
		return delegate.findPermisos(id);
	}

	@Override
	public void updatePermis(
			Long id,
			PermisDto permis) {
		delegate.updatePermis(id, permis);
	}

	@Override
	public void deletePermis(
			Long id,
			Long permisId) {
		delegate.deletePermis(id, permisId);
	}
    
	@Override
	public boolean checkIfAlreadyExistsWithCodi(
			Long entitatId,
			String codi, 
			Long grupId) {
		return delegate.checkIfAlreadyExistsWithCodi(
				entitatId,
				codi, 
				grupId);
	}
    
	@Override
	public void marcarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId) {
		delegate.marcarPerDefecte(
				entitatId,
				procedimentId,
				grupId);
	}

	@Override
	public List<GrupDto> findGrupsNoRelacionatAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long organGestorId) {
		return delegate.findGrupsNoRelacionatAmbMetaExpedient(
				entitatId,
				metaExpedientId,
				organGestorId);
	}

	@Override
	public void esborrarPerDefecte(
			Long entitatId,
			Long procedimentId,
			Long grupId) {
		delegate.esborrarPerDefecte(
				entitatId,
				procedimentId,
				grupId);
	}

	@Override
	public List<GrupDto> findGrups(
			Long entitatId,
			Long organGestorId,
			Long metaExpedientId) {
		return delegate.findGrups(
				entitatId,
				organGestorId,
				metaExpedientId);
	}

	@Override
	public GrupDto findGrupById(Long grupId) {
		return delegate.findGrupById(grupId);
	}

	@Override
	public GrupDto findGrupByExpedientPeticioAndProcedimentId(
			Long expedientPeticioId,
			Long procedimentId) {
		return delegate.findGrupByExpedientPeticioAndProcedimentId(
				expedientPeticioId,
				procedimentId);
	}

	@Override
	public List<GrupDto> findGrupsPermesosProcedimentsGestioActiva(
			Long entitatId,
			String rolActual,
			Long organGestorId) {
		return delegate.findGrupsPermesosProcedimentsGestioActiva(
				entitatId,
				rolActual,
				organGestorId);
	}

	@Override
	public boolean checkIfHasGrupPerDefecte(Long procedimentId) {
		return delegate.checkIfHasGrupPerDefecte(procedimentId);
	}
}