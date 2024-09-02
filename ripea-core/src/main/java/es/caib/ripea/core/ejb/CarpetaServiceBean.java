/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ExpedientCarpetaArbreDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.CarpetaService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class CarpetaServiceBean implements CarpetaService {

	@Autowired
	CarpetaService delegate;



	@Override
	@RolesAllowed("tothom")
	public CarpetaDto create(
			Long entitatId,
			Long contenidorId,
			String nom) {
		return delegate.create(
				entitatId,
				contenidorId,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public void update(
			Long entitatId,
			Long id,
			String nom) {
		delegate.update(
				entitatId,
				id,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public CarpetaDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public List<CarpetaDto> findByEntitatAndExpedient(Long entitatId, Long expedientId) throws NotFoundException {
		return delegate.findByEntitatAndExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ArbreDto<ExpedientCarpetaArbreDto>> findArbreCarpetesExpedient(Long entitatId, Long expedientId) {
		return delegate.findArbreCarpetesExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportIndexCarpetes(Long entitatId, Set<Long> carpetaIds, String format) throws IOException {
		return delegate.exportIndexCarpetes(entitatId, carpetaIds, format);
	}
	
}
