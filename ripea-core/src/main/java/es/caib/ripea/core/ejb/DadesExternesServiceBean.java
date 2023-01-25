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

import es.caib.ripea.core.api.dto.ComunitatDto;
import es.caib.ripea.core.api.dto.MunicipiDto;
import es.caib.ripea.core.api.dto.NivellAdministracioDto;
import es.caib.ripea.core.api.dto.PaisDto;
import es.caib.ripea.core.api.dto.ProvinciaDto;
import es.caib.ripea.core.api.service.DadesExternesService;

/**
 * Implementació de DadesExternesService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DadesExternesServiceBean implements DadesExternesService {

	@Autowired
	DadesExternesService delegate;


	@Override
	@RolesAllowed("tothom")
	public List<PaisDto> findPaisos() {
		return delegate.findPaisos();
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<ComunitatDto> findComunitats() {
		return delegate.findComunitats();
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<ProvinciaDto> findProvincies() {
		return delegate.findProvincies();
	}
	
	@Override
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) {
		return delegate.findProvinciesPerComunitat(comunitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		return delegate.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<NivellAdministracioDto> findNivellAdministracions() {
		return delegate.findNivellAdministracions();
	}

	@Override
	@RolesAllowed("tothom")
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		return delegate.findMunicipisPerProvinciaPinbal(provinciaCodi);
	}

}
