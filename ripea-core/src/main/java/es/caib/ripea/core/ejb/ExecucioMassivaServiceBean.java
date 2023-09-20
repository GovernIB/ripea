/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ElementTipusEnumDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExecucioMassivaService;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExecucioMassivaServiceBean implements ExecucioMassivaService {

	@Autowired
	ExecucioMassivaService delegate;

	@Override
	@RolesAllowed("tothom")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException, ValidationException {
		delegate.crearExecucioMassiva(entitatId, dto);
	}

	@Override
	@RolesAllowed("tothom")
	public void executarExecucioMassiva(Long cmasiu_id) throws NotFoundException, ValidationException {
		delegate.executarExecucioMassiva(cmasiu_id);
	}

	
	@Override
	@RolesAllowed("tothom")
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException {
		return delegate.findExecucionsMassivesPerUsuari(entitatId, usuari, pagina);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExecucioMassivaDto> findExecucionsMassivesGlobals() throws NotFoundException {
		return delegate.findExecucionsMassivesGlobals();
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException {
		return delegate.findContingutPerExecucioMassiva(exm_id);
	}

	public void comprovarExecucionsMassives() {
		delegate.comprovarExecucionsMassives();
	}

	@Override
	@RolesAllowed("tothom")
	public void saveExecucioMassiva(
			Long entitatId,
			ExecucioMassivaDto dto,
			List<ExecucioMassivaContingutDto> exc,
			ElementTipusEnumDto elementTipus) throws NotFoundException, ValidationException {
		delegate.saveExecucioMassiva(
				entitatId,
				dto,
				exc,
				elementTipus);
	}

}
